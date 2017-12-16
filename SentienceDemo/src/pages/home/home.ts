import { LoadingController } from 'ionic-angular/components/loading/loading';
import { DeviceListPage } from './../device-list/device-list';
import { Component } from '@angular/core';
import { NavController, AlertController, Platform } from 'ionic-angular';

declare var cordova: any;

@Component({
  selector: 'page-home',
  templateUrl: 'home.html'
})
export class HomePage {

  constructor(public nav: NavController, public alertCtrl: AlertController, public platform: Platform, public loadingCtrl: LoadingController) {

  }

  ionViewDidLoad() {

    this.platform.ready().then(() => {

      // cordova.plugins.TestPlugin.getData('', success => {
      //   console.log('getData call success: ' + success);

      //   this.nav.setRoot(DeviceListPage, {
      //     pm25: success.pm25,
      //     pm10: success.pm10,
      //     co2: success.co2,
      //     tvoc: success.tvoc,
      //     temperature: success.temperature,
      //     humidity: success.humidity
      //   });
      // }, error => {
      //   console.log('getData call error: ' + error);
      // });

      setTimeout(() => {
        this.nav.setRoot(DeviceListPage, {
          pm25: 25,
          pm10: 10,
          co2: 2.2,
          tvoc: 12.34,
          temperature: 15,
          humidity: 65
        });

      }, 5000);


    });

  }


}