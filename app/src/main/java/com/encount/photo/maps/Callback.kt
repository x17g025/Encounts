package com.encount.photo.maps

interface Callback {
    companion object {
        var ERROR = 0
        var SUCCESS = 1
    }

    /*コールバックメソッド*/
    fun callback(
        responseCode: Int, requestCode: Int,
        resultMap: Map<String, Any>
    )
}