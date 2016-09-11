# Installation instructions

On Debian Jessie (or equivalent)

## deb package
* Unpack deb
```terminal
dpkg --unpack /tmp/sohtserver_0.7.0~..._all.deb
```

* Install
```terminal
aptitude install sohtserver monit
```

## Tarball
### Install java
```terminal
aptitude -y install openjdk-7-jre-headless
```

### Monit
Optional
```terminal
aptitude -y install monit
```

### Install application
* Create user & group
```terminal
addgroup --system soht
adduser --system --home /var/run/sohtserver --ingroup soht --disabled-password --shell /bin/false soht
```

* Unpack
```terminal
tar xvzf /tmp/sohtserver_0.7.0-SNAPSHOT-distribution.tar.gz -C /opt
```

* Update ownerships
```terminal
chown -R soht:soht /opt/sohtserver
```

* Application symlink
```terminal
ln -s {/opt/sohtserver,}/usr/share/java/sohtserver.jar
```

* Configuration symlink
```terminal
ln -s {/opt/sohtserver,}/etc/default/sohtserver
ln -s {/opt/sohtserver,}/etc/sohtserver
```

* Logging directory
```terminal
mkdir /var/log/sohtserver
chown soht:soht /var/log/sohtserver
```

* Init scripts
```terminal
ln -s {/opt/sohtserver,}/etc/init.d/sohtserver
update-rc.d sohtserver defaults 92 08
invoke-rc.d sohtserver start
```

* Monit configuration
Only when monit is installed
```terminal
ln -s {/opt/sohtserver,}/etc/monit/conf.d/sohtserver.monitrc
```

* Monitoring and debug settings
```terminal
sed -i -e 's/#DEBUG#//g' /opt/torquerest/init.d/torquerest
```


## Configure monit
* service check interval
```terminal
sed -i -e 's/\(^  set daemon\) 120/\1 10 /' /etc/monit/monitrc
```

* enable embedded web server on localhost only
```terminal
sed -i -e 's/^#\( set httpd port 2812 and\)/ \1/' /etc/monit/monitrc
sed -i -e 's/^#\(    use address localhost\)/ \1/' /etc/monit/monitrc
sed -i -e 's/^#\(    allow localhost\)/ \1/' /etc/monit/monitrc
```

* Make sure monit is enabled
```terminal
sed -i -e 's/\(^START=\).*/\1yes/' /etc/default/monit
```

* Restart
```terminal
service monit restart
```
