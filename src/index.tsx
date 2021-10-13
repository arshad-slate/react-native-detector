import { NativeModules, NativeEventEmitter, Platform } from 'react-native';
const { Detector } = NativeModules;

enum EventsName {
  UserDidTakeScreenshot = 'UIApplicationUserDidTakeScreenshotNotification',
  ScreenCapturedDidChange = 'UIScreenCapturedDidChangeNotification',
}


export function addScreenshotListener(callback: Function) {
  if (Platform.OS === 'android') Detector.startScreenshotDetection();
  const eventEmitter = new NativeEventEmitter(Detector);
  eventEmitter.addListener(
    EventsName.UserDidTakeScreenshot,
    () => callback(),
    {}
  );

  return eventEmitter;
}

export function removeScreenshotListener(eventEmitter: NativeEventEmitter) {
  eventEmitter.removeAllListeners(EventsName.UserDidTakeScreenshot);
  if (Platform.OS === 'android') Detector.stopScreenshotDetection();
}

export function addScreenRecordListener(
  callback: (msgObj: { isRecording: boolean }) => void
) {
  if (Platform.OS === 'android') Detector.blockScreenRecording();
  const eventEmitter = new NativeEventEmitter(Detector);
  eventEmitter.addListener(EventsName.ScreenCapturedDidChange, callback, {});

  return eventEmitter;
}

export function removeScreenRecordListener(eventEmitter: NativeEventEmitter) {
  eventEmitter.removeAllListeners(EventsName.ScreenCapturedDidChange);
  if (Platform.OS === 'android') Detector.allowScreenRecording();
}

export function isRecordingScreen() {
   (Platform.OS === 'android')? null : Detector.isRecordingScreen();
}

export function preventScreenRecord() {
   (Platform.OS === 'ios')? Detector.preventScreenCapture(): Detector.blockScreenRecording();
}

export function allowScreenRecord() {
  if (Platform.OS === 'android') Detector.allowScreenRecording();
}
