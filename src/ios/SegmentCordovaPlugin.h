#import <Foundation/Foundation.h>
#import <Cordova/CDV.h>
#import <Analytics/SEGAnalytics.h>

@interface SegmentCordovaPlugin : CDVPlugin {
  // Member variables go here.
}

- (void) startWithConfiguration:(CDVInvokedUrlCommand*)command;
- (void) identify: (CDVInvokedUrlCommand*)command;
- (void) track: (CDVInvokedUrlCommand*)command;
- (void) screen: (CDVInvokedUrlCommand*)command;
- (void) group: (CDVInvokedUrlCommand*)command;
- (void) alias: (CDVInvokedUrlCommand*)command;
- (void) getAnonymousId: (CDVInvokedUrlCommand*)command;
- (void) reset: (CDVInvokedUrlCommand*)command;

@end
