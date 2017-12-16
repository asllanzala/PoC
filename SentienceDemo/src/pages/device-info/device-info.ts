import { SimpleAlert } from '../../providers/simple-alert';
import { Component } from '@angular/core';
import { NavController, NavParams, Platform } from 'ionic-angular';
import { Camera, File } from 'ionic-native';

/*
  Generated class for the DeviceInfo page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/

declare var cordova;

@Component({
  selector: 'page-device-info',
  templateUrl: 'device-info.html'
})
export class DeviceInfoPage {

  private deviceName: string;
  private pm25: string;
  private pm10: string;
  private co2: string;
  private tvoc: string;
  private temperature: string;
  private humidity: string;

  private song: string;

  constructor(public platform: Platform, public navCtrl: NavController,
    public navParams: NavParams, public simpleAlert: SimpleAlert) {
    this.deviceName = this.navParams.get('deviceName');
    this.pm25 = this.navParams.get('pm25');
    this.pm10 = this.navParams.get('pm10');
    this.co2 = this.navParams.get('co2');
    this.tvoc = this.navParams.get('tvoc');
    this.temperature = this.navParams.get('temperature');
    this.humidity = this.navParams.get('humidity');
  }

  ionViewDidLoad() {
    console.log('ionViewDidLoad DeviceInfoPage');

    cordova.plugins.TestPlugin.polling('', success => {
      console.log('polling call success: ' + success);
      this.pm25 = success.pm25;
      this.pm10 = success.pm10;
      this.co2 = success.co2;
      this.tvoc = success.tvoc;
      this.temperature = success.temperature;
      this.humidity = success.humidity;
    }, error => {
      console.log('polling call error: ' + error);
    });
  }

  play(): any {

    if (this.song == undefined) {
      let alert = this.simpleAlert.createAlert('No Music', 'Please select your favorate song.');
      alert.present();
    } else {
      cordova.plugins.TestPlugin.playMusic(this.song, success => {
        console.log('playMusic call success: ' + success);
      }, error => {
        console.log('playMusic call error: ' + error);
      });
    }

  }

  takePhoto(): any {
    cordova.plugins.TestPlugin.takePhoto('', success => {
      console.log('takePhoto call success: ' + success);
    }, error => {
      console.log('sentienceCall call error: ' + error);
    });
  }

}
