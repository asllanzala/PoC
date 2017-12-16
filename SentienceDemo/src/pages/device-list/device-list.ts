import { DeviceInfoPage } from './../device-info/device-info';
import { Component } from '@angular/core';
import { NavController, NavParams } from 'ionic-angular';

/*
  Generated class for the DeviceList page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-device-list',
  templateUrl: 'device-list.html'
})
export class DeviceListPage {

  constructor(public navCtrl: NavController, public navParams: NavParams) { }

  ionViewDidLoad() {
    console.log('ionViewDidLoad DeviceListPage');
  }

  goToDeviceInfo(): any {
    this.navCtrl.push(DeviceInfoPage, {
      deviceName: "Sentience Board",
      pm25: this.navParams.get('pm25'),
      pm10: this.navParams.get('pm10'),
      co2: this.navParams.get('co2'),
      tvoc: this.navParams.get('tvoc'),
      temperature: this.navParams.get('temperature'),
      humidity: this.navParams.get('humidity'),
    });
  }

}
