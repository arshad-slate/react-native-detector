#import "Detector.h"

@implementation Detector {
    UIView *_blockView;
}

RCT_EXPORT_MODULE();

RCT_EXPORT_BLOCKING_SYNCHRONOUS_METHOD(isRecordingScreen) {
    return @([self isRecording]);
}

RCT_EXPORT_METHOD(preventScreenCapture) {
    if (@available(iOS 11.0, *) ) {
      // If already recording, block it
      dispatch_async(dispatch_get_main_queue(), ^{
        [self preventScreenRecording];
      });

    }
}

- (instancetype)init {
  if (self = [super init]) {
    CGFloat boundLength = MAX([[UIScreen mainScreen] bounds].size.width, [[UIScreen mainScreen] bounds].size.height);
    _blockView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, boundLength, boundLength)];
    _blockView.backgroundColor = UIColor.whiteColor;
  }
  return self;
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
    [self preventScreenRecording];
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

- (void)preventScreenRecording {
  if (@available(iOS 11.0, *)) {
    BOOL isCaptured = [[UIScreen mainScreen] isCaptured];

    if (isCaptured) {
      [UIApplication.sharedApplication.keyWindow.subviews.firstObject addSubview:_blockView];
        [NSTimer scheduledTimerWithTimeInterval:1.5 repeats:NO block:^(NSTimer * _Nonnull timer) {
            [self->_blockView removeFromSuperview];
        }] ;
    } else {
      [_blockView removeFromSuperview];
    }
  }
}
@end
