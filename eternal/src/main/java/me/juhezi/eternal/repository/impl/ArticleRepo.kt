
package me.juhezi.eternal.repository.impl

import me.juhezi.eternal.repository.interfaces.IArticleRepo
import me.juhezi.eternal.repository.local.LocalArticleRepo
import me.juhezi.eternal.repository.remote.RemoteArticleRepo

/**
 * Auto generated by repo_generator.py
 */
class ArticleRepo(var localRepo: IArticleRepo = LocalArticleRepo(),
                  var remoteRepo: IArticleRepo = RemoteArticleRepo()) : IArticleRepo by localRepo {
    /*override fun add(t: Article, success: Success<Article>?, fail: Fail?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun update(t: Article, success: Success<Article>?, fail: Fail?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun remove(id: String, success: Success<Article>?, fail: Fail?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun query(id: String, success: Success<Article>?, fail: Fail?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun queryAll(success: Success<List<Article>>?, fail: Fail?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }*/
}