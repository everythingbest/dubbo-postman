/*
 * MIT License
 *
 * Copyright (c) 2019 everythingbest
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

function setProp(req,name,value){
    if(!req){
        rst.put("被替换属性对象不能为空",req);
        return false;
    }
    var errorName = "请求参数属性不存在";
    if(!name){
        rst.put(errorName,"替换属性必须指定");
        return false;
    }
    var obj = JSON.parse(req.body);
    var array = name.split(".");
    var length = array.length;
    var findObj = obj;
    for(var index in array){
        if(index == length -1){
            break;
        }
        var item = array[index];
        if(findObj){
            findObj = findObj[item];
            if(!findObj){
                rst.put(errorName,item);
                return false;
            }
        }else{
            var currentObj = obj[item];
            if(!currentObj){
                rst.put(errorName,item);
                return false;
            }
            findObj = currentObj;
        }

    }
    var lastItem = array[length -1];
    var existItem = findObj[lastItem];
    if(!existItem){
        rst.put(errorName,lastItem);
        return false;
    }else{
        findObj[lastItem] = value;//替换
        req.body = JSON.stringify(obj);
    }
    return true;
}

function getProp(req,name){
    if(!req){
        rst.put("属性对象不能为空",req);
        return false;
    }
    var errorName = "请求参数属性不存在";
    if(!name){
        rst.put(errorName,"属性名称必须指定");
        return false;
    }
    var obj = JSON.parse(req.body);
    var array = name.split(".");
    var length = array.length;
    var findObj = obj;
    for(var index in array){
        if(index == length -1){
            break;
        }
        var item = array[index];
        if(findObj){
            findObj = findObj[item];
            if(!findObj){
                rst.put(errorName,item);
                return false;
            }
        }else{
            var currentObj = obj[item];
            if(!currentObj){
                rst.put(errorName,item);
                return false;
            }
            findObj = currentObj;
        }

    }
    var lastItem = array[length -1];
    var existItem = findObj[lastItem];
    if(!existItem){
        rst.put(errorName,lastItem);
        return false;
    }else{
        return findObj[lastItem];
    }
}

function listItem(list,prop,value){
    for(var index in list){
        var item = list[index];
        var find = getInternalProp(item,prop);
        if(find == value){
            return true;
        }
    }
    return false;
}

function getInternalProp(item,name){
    if(!item){
        rst.put("属性对象不能为空",item);
        return false;
    }
    var errorName = "属性名称不存在";
    if(!name){
        rst.put(errorName,"属性名称必须指定");
        return false;
    }
    var obj = item;
    var array = name.split(".");
    var length = array.length;
    var findObj = obj;
    for(var index in array){
        if(index == length -1){
            break;
        }
        var item = array[index];
        if(findObj){
            findObj = findObj[item];
            if(!findObj){
                rst.put(errorName,item);
                return false;
            }
        }else{
            var currentObj = obj[item];
            if(!currentObj){
                rst.put(errorName,item);
                return false;
            }
            findObj = currentObj;
        }

    }
    var lastItem = array[length -1];
    var existItem = findObj[lastItem];
    if(!existItem){
        rst.put(errorName,lastItem);
        return false;
    }else{
        return findObj[lastItem];
    }
}

//调用java的等待函数
function wait(seconds){
    Packages.java.lang.Thread.sleep(seconds*1000);
}