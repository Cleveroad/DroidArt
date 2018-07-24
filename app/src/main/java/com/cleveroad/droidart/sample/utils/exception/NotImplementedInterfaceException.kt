package com.cleveroad.droidart.sample.utils.exception

class NotImplementedInterfaceException(clazz: Class<*>) :
        RuntimeException("NotImplementedInterfaceException: must implement" + clazz.simpleName)