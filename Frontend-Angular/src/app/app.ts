import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Navbar } from './navbar/navbar';
import { Footer } from './footer/footer';
import { Register } from "./register/register";

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Navbar, Footer, Register],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('Hoter Booking');
}
