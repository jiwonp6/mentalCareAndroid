package com.busanit.mentalcareandroid.model

data class Comment (val boardId : Long,
                    var commentContent: String,
                    val commentTime: String,
                    val userNickname : String,
                    val commentId : Long,
                    val childrenComments: List<ChildrenComment>)