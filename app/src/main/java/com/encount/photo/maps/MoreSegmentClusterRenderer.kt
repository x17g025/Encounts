package com.encount.photo.maps

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import com.encount.photo.R
import com.example.markerimpl.cluster.SegmentClusterItem
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator

class MoreSegmentClusterRenderer(context: Context, map: GoogleMap, manager: ClusterManager<SegmentClusterItem>) :
    DefaultClusterRenderer<SegmentClusterItem>(context, map, manager) {
    private val itemImageView: ImageView
    private val itemIconGenerator: IconGenerator = IconGenerator(context).apply {
        val iconView = LayoutInflater.from(context).inflate(R.layout.icon_segment, null, false).apply {
            itemImageView = findViewById(R.id.imageIcon)
        }
        setContentView(iconView)
    }

    private val clusterImageView: ImageView
    private val clusterTextView: TextView
    private val clusterIconGenerator: IconGenerator = IconGenerator(context).apply {
        val clusterView = LayoutInflater.from(context).inflate(R.layout.icon_segment_cluster, null, false).apply {
            clusterImageView = findViewById(R.id.imageIcon)
            clusterTextView = findViewById(R.id.textNumber)
        }
        setContentView(clusterView)
    }

    override fun onBeforeClusterItemRendered(item: SegmentClusterItem, markerOptions: MarkerOptions) {
        itemImageView.setImageResource(item.segment.imageResId)
        val icon = itemIconGenerator.makeIcon()
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon))
    }

    override fun onBeforeClusterRendered(cluster: Cluster<SegmentClusterItem>, markerOptions: MarkerOptions) {
        clusterImageView.setImageResource(R.drawable.app_logo)
        clusterTextView.text = cluster.size.toString()
        val icon = clusterIconGenerator.makeIcon()
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon))
    }

    override fun onClusterItemRendered(item: SegmentClusterItem, marker: Marker) {
        marker.tag = item.segment
        super.onClusterItemRendered(item, marker)
    }

    override fun shouldRenderAsCluster(cluster: Cluster<SegmentClusterItem>?): Boolean {
        // ClusterItemが一定距離内にいくつ集まったらクラスタ化するかをBooleanで返す
        return cluster?.size ?: 0 >= 5
    }
}