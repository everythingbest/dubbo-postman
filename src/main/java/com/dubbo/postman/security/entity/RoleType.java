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

package com.dubbo.postman.security.entity;

/**
 * @author everythingbest
 */
public enum RoleType {

    //各个角色定义
    DEV,QA,QAS,ADMIN;
    
    public static boolean compare(RoleType ori,RoleType tar){
    
        if(ori == RoleType.DEV){
        
            if(tar == RoleType.DEV || tar == RoleType.ADMIN){
                
                return true;
            }
        }
    
        if(ori == RoleType.QA){
        
            if(tar == RoleType.QA || tar == RoleType.ADMIN){
            
                return true;
            }
        }

        if(ori == RoleType.QAS){

            if(tar == RoleType.QAS || tar == RoleType.QA || tar == RoleType.ADMIN){

                return true;
            }
        }

        if(ori == RoleType.ADMIN){
        
            if(tar == RoleType.ADMIN){
            
                return true;
            }
        }
        
        return false;
    }
}
