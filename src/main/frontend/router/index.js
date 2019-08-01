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

import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

import Layout from '@/views/layout/Layout'
import Create from '@/views/pages/CreateService.vue'
import Access from '@/views/pages/AccessService.vue'
import Config from '@/views/pages/SystemConfig.vue'
import CaseManage from '@/views/pages/CaseManage.vue'

export const constantRouterMap = [
    {
        path: '/redirect',
        component: Layout,
        hidden: true,
        children: [
            {
                path: '/redirect/:path*',
                component: () => import('@/views/redirect/index')
            }
        ]
    },
    {
        path: '/',
        component: Layout,
        redirect: '/access/index',
        name: 'accessService',
        hidden: true,
        children: [{
            path: 'access',
            component: Access
        }]
    },
    {
        path: '/access',
        component: Layout,
        redirect: '/access/index',
        children: [
            {
                path: 'index',
                component: Access,
                meta: { title: '访问服务', icon: 'table' },
                name: 'accessService'
            },
        ]
    },
    {
        path: '/case-manage',
        component: Layout,
        redirect: '/case-manage/index',
        children: [
            {
                path: 'index',
                component: CaseManage,
                meta: { title: '场景测试', icon: 'nested' },
                name: 'sceneManage'
            },
        ]
    },
    {
        path: '/create',
        redirect: '/create/index',
        component: Layout,
        children: [
            {
                path: 'index',
                component: Create,
                meta: { title: '创建服务', icon: 'tab' },
                name: 'createService'
            },
        ]
    },
    {
        path: '/config',
        component: Layout,
        children: [
            {
                path: 'index',
                component: Config,
                meta: { title: '注册中心', icon: 'list' },
                name: 'systemConfig'
            },
        ]
    },
    {
        path: 'external-link',
        component: Layout,
        children: [
            {
                path: 'https://github.com/everythingbest/dubbo-postman/tree/master',
                meta: { title: '使用帮助', icon: 'guide' }
            }
        ]
    },
    {
        path: '/404',
        meta: { title: '404页面'},
        component: () => import('@/views/error-page/404'),
        hidden: true
    },
    {
        path: '/401',
        meta: { title: '401页面'},
        component: () => import('@/views/error-page/401'),
        hidden: true
    },
    { path: '*', redirect: '/404', hidden: true }
]

export default new Router({
    // mode: 'history', //后端支持可开
    scrollBehavior: () => ({ y: 0 }),
    routes: constantRouterMap
})
