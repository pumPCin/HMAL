package com.android.hml.service

import android.os.IBinder
import android.os.IBinder.DeathRecipient
import android.os.Parcel
import android.os.RemoteException
import android.os.ServiceManager
import com.android.hml.common.Constants
import com.android.hml.common.IHMLService
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

object ServiceClient : IHMLService, DeathRecipient {

    private const val TAG = "SC"

    private class ServiceProxy(private val obj: IHMLService) : InvocationHandler {
        override fun invoke(proxy: Any?, method: Method, args: Array<out Any?>?): Any? {
            val result = method.invoke(obj, *args.orEmpty())
            return result
        }
    }

    @Volatile
    private var service: IHMLService? = null

    fun linkService(binder: IBinder) {
        service = Proxy.newProxyInstance(
            javaClass.classLoader,
            arrayOf(IHMLService::class.java),
            ServiceProxy(IHMLService.Stub.asInterface(binder))
        ) as IHMLService
        binder.linkToDeath(this, 0)
    }

    private fun getServiceLegacy(): IHMLService? {
        if (service != null) return service
        val pm = ServiceManager.getService("package")
        val data = Parcel.obtain()
        val reply = Parcel.obtain()
        val remote = try {
            data.writeInterfaceToken(Constants.DESCRIPTOR)
            data.writeInt(Constants.ACTION_GET_BINDER)
            pm.transact(Constants.TRANSACTION, data, reply, 0)
            reply.readException()
            val binder = reply.readStrongBinder()
            IHMLService.Stub.asInterface(binder)
        } catch (e: RemoteException) {
            null
        } finally {
            data.recycle()
            reply.recycle()
        }
        if (remote != null) {
            remote.asBinder().linkToDeath(this, 0)
            service = Proxy.newProxyInstance(
                javaClass.classLoader,
                arrayOf(IHMLService::class.java),
                ServiceProxy(remote)
            ) as IHMLService
        }
        return service
    }

    override fun binderDied() {
        service = null
    }

    override fun asBinder() = service?.asBinder()

    override fun getServiceVersion() = getServiceLegacy()?.serviceVersion ?: 0

    override fun syncConfig(json: String) {
        getServiceLegacy()?.syncConfig(json)
    }

    override fun stopService(cleanEnv: Boolean) {
        getServiceLegacy()?.stopService(cleanEnv)
    }
}
