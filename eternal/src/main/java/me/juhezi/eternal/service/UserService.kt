package me.juhezi.eternal.service

import me.juhezi.eternal.model.User

/**
 * 所提供的服务：
 * 1. 查询当前登录用户的信息
 * 2. 登录
 * 3. 注销
 * 4. 修改用户信息
 *
 * 注：不包含用户界面
 *
 * Created by Juhezi[juhezix@163.com] on 2018/7/24.
 */
class UserService {

    private var mCurrentUser : User? = null

    init {

    }

    /**
     * 注销
     */
    fun signOut() {}

    /**
     * 登录
     * 参数待定
     */
    fun signIn() {

    }

}

// sign up - 注册
// sign out - 注销
// sign in - 登录
