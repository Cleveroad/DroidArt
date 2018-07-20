package com.cleveroad.droidwordart.utils.exception

class NotImplementedInterfaceException(clazz: Class<*>) : RuntimeException("NotImplementedInterfaceException: must implement" + clazz.simpleName)