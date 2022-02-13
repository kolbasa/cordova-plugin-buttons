module.exports = function (context) {

    let fs = require('fs');
    let path = require('path');
    let UTF8 = 'utf8';

    if (!context.opts.platforms.includes('android')) {
        return;
    }

    const platformRoot = path.join(context.opts.projectRoot, 'platforms/android/app/src/main/java');
    let configXmlPath = path.join(context.opts.projectRoot, 'config.xml');

    let configXml = fs.readFileSync(configXmlPath, UTF8);
    let appId = configXml.split(' id=\"').pop().split('\"')[0];
    let packageName = appId.replace(/\./g, '/');

    const MainActivity = platformRoot + '/' + packageName + '/MainActivity.java';
    data = fs.readFileSync(MainActivity, UTF8);

    if (data.includes('greenrobot')) {
        return;
    }

    data = data.replace(
        /import org.apache.cordova.*;/g,
        'import org.apache.cordova.*;\n' +
        'import android.view.KeyEvent;\n' +
        'import org.greenrobot.eventbus.EventBus;'
    );

    data = data.replace(
        /@Override/g,
        '@Override\n' +
        '    public boolean dispatchKeyEvent(KeyEvent keyEvent) {\n' +
        '        boolean result = super.dispatchKeyEvent(keyEvent);\n' +
        '        EventBus.getDefault().post(keyEvent);\n' +
        '        return result;\n' +
        '    }\n\n' +
        '    @Override'
    );

    fs.writeFileSync(MainActivity, data, UTF8);

};