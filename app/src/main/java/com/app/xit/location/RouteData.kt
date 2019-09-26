package com.app.xit.location

data class RouteData(val geocoded_waypoints: List<Geocoded_waypoints>?, val routes: List<Routes>?, val status: String?)

data class Bounds(val northeast: Northeast?, val southwest: Southwest?)

data class Distance(val text: String?, val value: Number?)

data class Duration(val text: String?, val value: Number?)

data class End_location(val lat: Number?, val lng: Number?)

data class Geocoded_waypoints(val geocoder_status: String?, val place_id: String?, val types: List<String>?)

data class Legs(val distance: Distance?, val duration: Duration?, val end_address: String?, val end_location: End_location?, val start_address: String?, val start_location: Start_location?, val steps: List<Steps>?, val traffic_speed_entry: List<Any>?, val via_waypoint: List<Any>?)

data class Northeast(val lat: Number?, val lng: Number?)

data class Overview_polyline(val points: String?)

data class Polyline(val points: String?)

data class Routes(val bounds: Bounds?, val copyrights: String?, val legs: List<Legs>?, val overview_polyline: Overview_polyline?, val summary: String?, val warnings: List<Any>?, val waypoint_order: List<Any>?)

data class Southwest(val lat: Number?, val lng: Number?)

data class Start_location(val lat: Number?, val lng: Number?)

data class Steps(val distance: Distance?, val duration: Duration?, val end_location: End_location?, val html_instructions: String?, val maneuver: String?, val polyline: Polyline?, val start_location: Start_location?, val travel_mode: String?)
