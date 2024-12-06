import {createRouter, createWebHashHistory} from 'vue-router'

import Home from '@/views/Home';
import Index from '@/views/Index';
import LeastSquares from '@/views/LeastSquares';
import StockManage from '@/views/StockManage';
import UserManage from '@/views/UserManage';
import Login from '@/views/Login';
import Register from '@/views/Register';
import RandomForest from '@/views/RandomForest';
import LSTM from '@/views/LSTM';
import LSTMManage from '@/views/LSTMManage';
import RandomForestManage from '@/views/RandomForestManage';
import KNN from '@/views/KNN';
import StockMinuteManage from '@/views/StockMinuteManage';
import Analyse from '@/views/Analyse';
import {postRequest} from '@/utils/api';

const routes = [
    {
        path: '/',
        redirect: '/login',
    },
    {
        path: '/login',
        name: 'Login',
        component: Login,
    },
    {
        path: '/register',
        name: 'Register',
        component: Register,
    },
    {
        path: '/home',
        name: 'Home',
        component: Home,
        redirect: '/index',
        children: [
            {
                path: '/index',
                name: 'Index',
                component: Index,
            },
            {
                path: '/leastSquares',
                name: 'LeastSquares',
                component: LeastSquares,
            },
            {
                path: '/stockManage',
                name: 'StockManage',
                component: StockManage,
            },
            {
                path: '/userManage',
                name: 'UserManage',
                component: UserManage,
            },
            {
                path: '/randomForest',
                name: 'RandomForest',
                component: RandomForest,
            },
            {
                path: '/LSTM',
                name: 'LSTM',
                component: LSTM,
            },
            {
                path: '/LSTMManage',
                name: 'LSTMManage',
                component: LSTMManage,
            },
            {
                path: '/randomForestManage',
                name: 'RandomForestManage',
                component: RandomForestManage,
            },
            {
                path: '/KNN',
                name: 'KNN',
                component: KNN,
            },
            {
                path: '/stockMinuteManage',
                name: 'StockMinuteManage',
                component: StockMinuteManage,
            },
            {
                path: '/analyse',
                name: 'Analyse',
                component: Analyse,
            },
        ]
    },
]

const router = createRouter({
    history: createWebHashHistory(),
    routes
})

router.beforeEach((to, from, next) => {
    if (to.name === "Login" && localStorage.getItem("token")) {
        postRequest("/user/login", {
            username: localStorage.getItem("username"),
            password: localStorage.getItem("password")
        }).then((response) => {
            if (response.data.code === 200) {
                const user = response.data.data;
                localStorage.setItem("username", user.username);
                localStorage.setItem("password", user.password);
                localStorage.setItem("avatarUrl", user.avatarUrl);
                localStorage.setItem("token", user.token);
                localStorage.setItem("userType", user.userType);
                next("/home");
            } else {
                localStorage.removeItem("username");
                localStorage.removeItem("password");
                localStorage.removeItem("avatarUrl");
                localStorage.removeItem("token");
                localStorage.removeItem("userType");
                next("/login");
            }
        });
    } else if (to.name === "Login" || to.name === "Register") {
        next();
    } else if (localStorage.getItem("token")) {
        next();
    } else {
        next("/login");
    }
})

export default router