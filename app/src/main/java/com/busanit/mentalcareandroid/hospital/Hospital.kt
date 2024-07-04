package com.busanit.mentalcareandroid.hospital

data class Hospital(val hospitalId: String, val hospitalName: String, val hospitalLocation: String, val hospitalWebsite: String, val hospitalCall: String,
                    val monStartTime: String, val monEndTime: String, val tueStartTime: String, val tueEndTime: String, val wedStartTime: String,
                    val wedEndTime: String, val thuStartTime: String, val thuEndTime: String, val friStartTime: String, val friEndTime: String,
                    val satStartTime: String, val satEndTime: String, val sunStartTime: String, val sunEndTime: String, val sunHoliday: String, val lunchtime: String, val holiday: String)