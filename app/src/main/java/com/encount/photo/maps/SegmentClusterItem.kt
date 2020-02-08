package com.encount.photo.maps

import com.encount.photo.maps.Segment
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class SegmentClusterItem(val segment: Segment) : ClusterItem {
    override fun getSnippet(): String = segment.flowerName

    override fun getTitle(): String = segment.title

    override fun getPosition(): LatLng = segment.coordinate
}