import { Routes } from '@angular/router';
import { Login } from './login/login';
import { Register } from './register/register';
import { Guard } from './service/guard';

export const routes: Routes = [
    { path: 'login', component: Login },
    { path: 'register', component: Register },
    {
        path: 'profile',
        loadComponent: () =>
            import('./profile/profile').then(m => m.Profile),
        canActivate: [Guard]
    },
    {
        path: 'edit-profile',
        loadComponent: () =>
            import('./editprofile/editprofile').then(m => m.Editprofile),
        canActivate: [Guard]
    },
    { path: '**', redirectTo: 'login' }
];
