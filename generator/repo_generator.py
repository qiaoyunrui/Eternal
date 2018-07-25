# -*- coding:utf-8 -*-
# repo 代码生成器
# waiting

from sys import argv

# print("length is", len(argv))
# print("list is", str(argv))

if len(argv) <= 1:
    print("Please input the model name!")
    exit(1)

# 模块名字
model_name = argv[1].strip()
if len(model_name) == 0:
    print("The model name can not be empty!")
    exit(2)

if not model_name.istitle():
    print("The model name must start with uppercase letter.")
    exit(3)

print("The model name is \"", model_name, "\"")

mate = "model_name"
field = "${" + mate + "}"

interface = """
package me.juhezi.eternal.repository.interfaces

/**
 * Auto generated by repo_generator.py
 */
interface I${model_name}Repo : IRepo<${model_name}>
"""

local = """
package me.juhezi.eternal.repository.local

/**
 * Auto generated by repo_generator.py
 */
class Local${model_name}Repo : I${model_name}Repo {
    override fun add(t: ${model_name}, success: Success<${model_name}>?, fail: Fail?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun update(t: ${model_name}, success: Success<${model_name}>?, fail: Fail?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun remove(id: String, success: Success<${model_name}>?, fail: Fail?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun query(id: String, success: Success<${model_name}>?, fail: Fail?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun queryAll(success: Success<List<${model_name}>>?, fail: Fail?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
"""

remote = """
package me.juhezi.eternal.repository.remote

/**
 * Auto generated by repo_generator.py
 */
class Remote${model_name}Repo : I${model_name}Repo {
    override fun add(t: ${model_name}, success: Success<${model_name}>?, fail: Fail?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun update(t: ${model_name}, success: Success<${model_name}>?, fail: Fail?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun remove(id: String, success: Success<${model_name}>?, fail: Fail?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun query(id: String, success: Success<${model_name}>?, fail: Fail?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun queryAll(success: Success<List<${model_name}>>?, fail: Fail?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
"""

impl = """
package me.juhezi.eternal.repository.impl

/**
 * Auto generated by repo_generator.py
 */
class ${model_name}Repo(var localRepo: I${model_name}Repo = Local${model_name}Repo(),
               var remoteRepo: I${model_name}Repo = Remote${model_name}Repo()) : I${model_name}Repo by remoteRepo {
    override fun add(t: ${model_name}, success: Success<${model_name}>?, fail: Fail?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun update(t: ${model_name}, success: Success<${model_name}>?, fail: Fail?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun remove(id: String, success: Success<${model_name}>?, fail: Fail?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun query(id: String, success: Success<${model_name}>?, fail: Fail?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun queryAll(success: Success<List<${model_name}>>?, fail: Fail?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
"""


def replace(content):
    return content.replace(field, model_name)


interface = replace(interface)
local = replace(local)
remote = replace(remote)
impl = replace(impl)
print(interface)
print(local)
print(remote)
print(impl)
