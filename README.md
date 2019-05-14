# netbox-bulk-connect

Netbox Bulk Connect is a tool to connect all devices with multiple rear ports in netbox automatically.
The tedious task of connecting all rearports manually is not necessary anymore.

## Configurations
When first starting the software, it is necessary to change the settings under File -> Settings.
The settings are set by default to server "netbox" and an http encryption.

If you only want to use the Option *CSV Import*, a token is not required and the field can be left blank. To get your API Token, take a look at netbox -> Profiles -> API Tokens.

IMPORTANT: If you want to use the Option *Automatic Entry*, you have to select a Token with rights to **read and write**.
For this purpose, when creating/editing the API token, within netbox, activate the tick in the checkbox "Write enabled".

## Select Devices
All devices with existing rearports, which are not connected, are automatically retrieved from netbox.
* in "Device A", select the device you want to connect
* in "Device B", select the device you want to connect to the device "Device A"
* the number of ports is determined automatically
* cable types are imported from netbox
* the cable length should be indicated in meters and only whole numbers without decimal places can be entered

## Connect rearports
There are two ways to connect the rear ports 

### Option 1 - CSV Import:
With a click on "Print CSV format" you can print out the connections and then copy them to the clipboard with a click on "Copy".
Now paste the copied text into netbox under Devices -> Cables -> Import, and "Submit" to create all rear port connections.

### Option 2 - Automatic Entry:
By clicking on "Connect Rear Ports", the rear ports of the selected devices are automatically connected in netbox.

## Screenshots

![Screenshot 1](https://s18.directupload.net/images/190513/bw7g4c23.png)

![Screenshot 2](https://s18.directupload.net/images/190513/6aln4ht7.png)

![Screenshot 3](https://s18.directupload.net/images/190513/epjn46vq.png)

***
How to get your API Token
<br>

![Screenshot 4](https://s18.directupload.net/images/190514/55b3mova.png)