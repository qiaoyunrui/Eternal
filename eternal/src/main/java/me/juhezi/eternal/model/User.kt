package me.juhezi.eternal.model

import io.realm.RealmObject

/**
 * 用户实体类
 * - id
 * - username 用户名
 * - nickname 昵称
 * - password 密码
 * - ...
 */
data class User(var id: String, var username: String, var password: String, var nickname: String) : RealmObject()