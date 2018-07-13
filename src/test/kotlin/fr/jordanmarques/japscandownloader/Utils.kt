package fr.jordanmarques.japscandownloader

import kotlin.reflect.KClass

fun getResource(path: String, kClass: KClass<out Any>): String {
    return kClass::class.java.classLoader.getResource(path).readText()
}