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

import 'font-awesome/css/font-awesome.min.css'

import VueCodemirror from 'vue-codemirror'
import 'codemirror/lib/codemirror.css'
import'codemirror/addon/fold/foldgutter.css'
import'codemirror/addon/fold/brace-fold.js'
import'codemirror/addon/fold/comment-fold.js'
import'codemirror/addon/fold/foldcode.js'
import'codemirror/addon/fold/foldgutter.js'
import'codemirror/addon/fold/indent-fold.js'
import'codemirror/addon/fold/markdown-fold.js'
import'codemirror/addon/fold/xml-fold.js'
import 'codemirror/mode/javascript/javascript';
import 'codemirror/addon/scroll/annotatescrollbar.js'
import 'codemirror/addon/search/match-highlighter.js'
import 'codemirror/addon/search/searchcursor.js'
import 'codemirror/addon/search/search.js'
import 'codemirror/addon/search/jump-to-line.js'
import 'codemirror/addon/search/matchesonscrollbar.css'
import 'codemirror/addon/search/matchesonscrollbar.js'
import 'codemirror/addon/display/fullscreen.js'
import 'codemirror/addon/display/fullscreen.css'
import 'codemirror/theme/monokai.css'
import 'codemirror/theme/eclipse.css'
import 'codemirror/theme/zenburn.css'
import 'codemirror/theme/darcula.css'
import 'codemirror/theme/elegant.css'
import 'codemirror/addon/dialog/dialog.css'
import 'codemirror/addon/dialog/dialog.js'

import NProgress from 'nprogress';
import 'nprogress/nprogress.css'

import VueClipboard  from 'vue-clipboard2' //复制功能

import 'normalize.css/normalize.css' // A modern alternative to CSS resets

import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import locale from 'element-ui/lib/locale/lang/zh-CN' // lang i18n
import 'element-ui/lib/theme-chalk/base.css'; // fade/zoom 等
import CollapseTransition from 'element-ui/lib/transitions/collapse-transition'

import getPageTitle from '@/utils/get-page-title'

import '@/styles/index.scss' // global css

import App from './App'
import router from './router'
import store from './store'
import '@/icons'

//如果是本地调试就使用,在build的时候正注释掉这行
//import './mock'

Vue.prototype.$NProgress = NProgress;
Vue.component('collapse-transition', CollapseTransition)
Vue.use( VueClipboard )
Vue.use(ElementUI, { locale })
Vue.use(VueCodemirror)

Vue.config.productionTip = false

NProgress.configure({
        showSpinner: false
    });

router.beforeEach(async(to, from, next) => {
    NProgress.start()
    document.title = getPageTitle(to.meta.title)
    next()
})

router.afterEach(() => {
    NProgress.done()
});

new Vue({
    el: '#app',
    router,
    store,
    render: h => h(App)
})