import { SimpleAlert } from './../providers/simple-alert';
import { DeviceListPage } from './../pages/device-list/device-list';
import { DeviceInfoPage } from './../pages/device-info/device-info';
import { NgModule, ErrorHandler } from '@angular/core';
import { IonicApp, IonicModule, IonicErrorHandler } from 'ionic-angular';
import { MyApp } from './app.component';
import { HomePage } from '../pages/home/home';

@NgModule({
  declarations: [
    MyApp,
    HomePage,
    DeviceInfoPage,
    DeviceListPage
  ],
  imports: [
    IonicModule.forRoot(MyApp)
  ],
  bootstrap: [IonicApp],
  entryComponents: [
    MyApp,
    HomePage,
    DeviceInfoPage,
    DeviceListPage
  ],
  providers: [{provide: ErrorHandler, useClass: IonicErrorHandler}, SimpleAlert]
})
export class AppModule {}
