import { createRouter, createWebHistory } from 'vue-router'
import Main from "../views/Main";
import Split from "../views/split/Split";
import Merge from "../views/merge/Merge";


const router = createRouter({
    history: createWebHistory(),
    hashbang: false,
    mode: 'html5',
    linkActiveClass: 'active',
    transitionOnLoad: true,
    root: '/',
    routes: [
        { path: '/', component: Main, alias: '' },
        { path: '/split', component: Split },
        { path: '/merge', component: Merge }
    ]
})

export default router
