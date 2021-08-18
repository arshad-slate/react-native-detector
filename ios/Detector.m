#import "Detector.h"

@implementation Detector

RCT_EXPORT_MODULE();

RCT_EXPORT_BLOCKING_SYNCHRONOUS_METHOD(isRecordingScreen) {
    return @([self isRecording]);
}
- (NSArray<NSString *> *)supportedEvents {
    return @[@"UIApplicationUserDidTakeScreenshotNotification", @"UIScreenCapturedDidChangeNotification"];
}

- (void)startObserving {
    NSNotificationCenter *center = [NSNotificationCenter defaultCenter];
    [center addObserver:self
    selector:@selector(sendNotificationToRN:)
    name:UIApplicationUserDidTakeScreenshotNotification
    object:nil];
    
    if (@available(iOS 11.0, *)) {
        [center addObserver:self
                   selector:@selector(sendScreenRecordNotificationToRN:)
                       name:UIScreenCapturedDidChangeNotification
                     object:nil];
    } else {
        // Fallback on earlier versions
    }

}

- (void)stopObserving {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)sendNotificationToRN:(NSNotification *)notification {
    [self sendEventWithName:notification.name
                   body:nil];
}

- (void)sendScreenRecordNotificationToRN:(NSNotification *)notification {
    [self sendEventWithName:notification.name
                       body:@{@"isRecording": @([self isRecording])}];
}

- (BOOL) isRecording
{
    if (@available(iOS 11.0, *)) {
        return [[UIScreen mainScreen] isCaptured];
    } else {
        // Fallback on earlier versions
        return false;
    }
}

@end
