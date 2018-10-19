package sample

import jni.*
import kotlinx.cinterop.*


@Suppress("unused")
@CName("Java_HelloJNI_sayHello")
fun Java_HelloJNI_sayHello(env: JNIEnvVar, thiz: jobject) {
    JContext(env, thiz).apply {
        //println("Java_HelloJNI_sayHello")
        val class_java_lang_System = env.findClass("java/lang/System")
        val class_java_io_PrintStream = env.findClass("java/io/PrintStream")
        //println("cls: $class_java_lang_System")
        val outField = env.getStaticFieldID(class_java_lang_System!!, "out", "Ljava/io/PrintStream;")
        //println("outField: $outField")
        val out = env.getStaticObjectField(class_java_lang_System, outField!!)
        //println("out: $out")

        //val method_java_io_PrintStream_println = env.getMethodID(class_java_io_PrintStream!!, "println", "(Ljava/lang/String;)V")

        val method_java_io_PrintStream_println = JMethod(JClass("java/io/PrintStream"), "println", "(Ljava/lang/String;)V")

        val str = env.newString("HI FROM Kotlin/Native!")
        //env.callVoid(out!!, method_java_io_PrintStream_println!!, str!!.uncheckedCast())

        method_java_io_PrintStream_println.invokeVoid(out!!, str!!.uncheckedCast())


        /*
        env.getMethodID(cls!!, "")
        val str = env.newString("HI!")
        env.pointed.GetMethodID
        env.pointed!!.CallStaticVoidMethodA()
        //env.pointed.CallStaticVoidMethodA!!.invoke()
        //println("Hello World from Kotlin! $cls")
        */
        //println("Hello World from Kotlin! env=$env, cls=$cls, outField=$outField")
    }

    return
}

//fun main(args: Array<String>) {
//    println(hello())
//}
//
//fun hello(): String = "Hello, Kotlin/Native!"

fun JNIEnvVar.findClass(fqname: String): jclass? = memScoped {
    val receiver = this@findClass
    receiver.pointed?.FindClass?.invoke(receiver.uncheckedCast(), fqname.cstr.ptr)
}

fun JNIEnvVar.getStaticFieldID(clz: jclass, name: String, sig: String): jfieldID? = memScoped {
    val receiver = this@getStaticFieldID
    receiver.pointed?.GetStaticFieldID?.invoke(receiver.uncheckedCast(), clz, name.cstr.ptr, sig.cstr.ptr)
}

fun JNIEnvVar.getFieldID(clz: jclass, name: String, sig: String): jfieldID? = memScoped {
    val receiver = this@getFieldID
    receiver.pointed?.GetFieldID?.invoke(receiver.uncheckedCast(), clz, name.cstr.ptr, sig.cstr.ptr)
}

fun JNIEnvVar.getStaticObjectField(clz: jclass, field: jfieldID): jobject? = memScoped {
    val receiver = this@getStaticObjectField
    receiver.pointed?.GetStaticObjectField?.invoke(receiver.uncheckedCast(), clz, field)
}

fun JNIEnvVar.getStaticMethodID(clz: jclass, name: String, sig: String): jmethodID? = memScoped {
    val receiver = this@getStaticMethodID
    receiver.pointed?.GetStaticMethodID?.invoke(receiver.uncheckedCast(), clz, name.cstr.ptr, sig.cstr.ptr)
}

fun JNIEnvVar.getMethodID(clz: jclass, name: String, sig: String): jmethodID? = memScoped {
    val receiver = this@getMethodID
    receiver.pointed?.GetMethodID?.invoke(receiver.uncheckedCast(), clz, name.cstr.ptr, sig.cstr.ptr)
}

fun JNIEnvVar.newString(str: String): jstring? = memScoped {
    val receiver = this@newString
    receiver.pointed?.NewStringUTF?.invoke(receiver.uncheckedCast(), str.cstr.ptr)
}

fun JNIEnvVar.callStaticVoid(clz: jclass, method: jmethodID, vararg args: jvalue): Unit = memScoped {
    //pointed?.CallStaticVoidMethodA?.invoke(uncheckedCast(), str.cstr.ptr)
    TODO()
}

fun JNIEnvVar.callVoid(obj: jobject, method: jmethodID, vararg args: jvalue): Unit = memScoped {
    val receiver = this@callVoid
    val data = allocArray<LongVar>(args.size)
    for (n in 0 until args.size) data[n] = args[n].uncheckedCast()
    receiver.pointed?.CallVoidMethodA?.invoke(receiver.uncheckedCast(), obj, method, data.uncheckedCast())
    Unit
}

// TYPED

data class JClass(val className: String)
data class JField(val clazz: JClass, val name: String, val sig: String)
data class JMethod(val clazz: JClass, val name: String, val sig: String)

class JContext(val env: JNIEnvVar, val thiz: jobject) {
    fun JMethod.invokeVoid(obj: jobject, vararg args: jvalue) {
        val clazz = env.findClass(clazz.className)
        val method = env.getMethodID(clazz!!, name, sig)
        env.callVoid(obj, method!!, *args)
    }

    fun JMethod.staticInvokeVoid(vararg args: jvalue) {
        val clazz = env.findClass(clazz.className)
        val method = env.getStaticMethodID(clazz!!, name, sig)
        env.callStaticVoid(clazz, method!!, *args)
    }
}