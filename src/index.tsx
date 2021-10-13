import {NativeEventEmitter, NativeModules, Platform} from 'react-native';

const {Detector} = NativeModules;

enum EventsName {
  UserDidTakeScreenshot = 'UIApplicationUserDidTakeScreenshotNotification',
  ScreenCapturedDidChange = 'UIScreenCapturedDidChangeNotification',
}

const isiOS = Platform.OS === 'ios'

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
  if (!isiOS) Detector.stopScreenshotDetection();
}

export function addScreenRecordListener(
  callback: (msgObj: { isRecording: boolean }) => void
) {
  // Prevents screen record.
  if (Platform.OS === 'android') preventScreenRecord();
  const eventEmitter = new NativeEventEmitter(Detector);
  eventEmitter.addListener(EventsName.ScreenCapturedDidChange, callback, {});

  return eventEmitter;
}

export function removeScreenRecordListener(eventEmitter: NativeEventEmitter) {
  eventEmitter.removeAllListeners(EventsName.ScreenCapturedDidChange);
  // Allows screen record.
  if (!isiOS) Detector.allowScreenRecord();
}

export function isRecordingScreen() {
  
  return isiOS ? 
    Detector.isRecordingScreen() 
    :
    // Always returns null for android.
    null;
}

export function preventScreenRecord() {
  isiOS ? 
    // ios screen capture prevention.
    Detector.preventScreenCapture() 
    :
    // Makes android screen recording blank.
    Detector.blockScreenRecording();

}

export function allowScreenRecord() {
  if (!isiOS) Detector.allowScreenRecording();
}
