package com.busanit.mentalcareandroid.model

import org.w3c.dom.Comment

data class Board(val boardTitle: String,
                  val boardContent: String,
                  val boardLikeCount : Int,
                  val boardCommentCount : Int,
                  val calculateTime : String,
                  val userNickname: String,
                  val boardTag: String,
                  val boardId: Long,
                  val comments : List<Comment>)