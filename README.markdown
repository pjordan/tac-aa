 TAC/Ad Auctions
====================
Internet advertising provides a substantial source of revenue for online publishers, amounting to billions of dollars annually. Sponsored search is a popular form of targeted advertising, in which query-speciﬁc advertisements are placed alongside organic search-engine results.  In popular search engines, the placement (position) of an ad for a given query, along with the cost (to the advertiser) per click (CPC), is determined through an auction process.  Advertisers wishing to place their ads on relevant searchers must strategically decide which keywords to bid on, and what CPC prices to offer.

The TAC Ad Auction (TAC/AA) game presents a realistic sponsored search environment for a simulated advertising scenario.  Advertisers represent retailers in a simplified home entertainment market, bidding to place ads before users searching on product keywords.  Game entrants design and implement bidding strategies for the advertisers, while behaviors of the search engine and users are simulated by the server.  The TAC/AA tournament pits advertiser agent strategies against each other, evaluating each in terms of sales profit net of advertising costs.

Our hope is that by tackling this challenging problem competitively, researchers and practitioners participating in TAC/AA will produce new ideas about bidding strategy for advertising (and in general), as well as insights about sponsored-search mechanisms and ways to improve the model.

Building TAC/AA
---------------------

The TAC/AA game server sources and how to create the executable.

### Prerequisites:
* Install java sdk (1.6+)
* Install maven (3.0.1+) [http://maven.apache.org]

### Install the jars 

Checkout or download the latest repository branch (this will need to change to a public server):

    git clone http://github.com/pjordan/tac-aa

Install the modules:

    mvn install

### Create the server binary

Change directories into aa-server

    cd aa-server

Assemble the binaries

    mvn assembly:assembly

The binaries are now assembled in target

    ls target/*.{zip,gz}

Running the server
---------------------

To following software is required to run the server

* java (1.6+) 

Optionally:

* mysql (recommended)

After downloading the server archive, extract the server in a convenient location. You will need to configure the server before starting up.

### Configuring the server

After unpacking the server archive, three sub-directories and one file should exist in the new directory. To configure the server, modify the

    config/server.conf

file to conform to your desired setup.

If you are running the server publicly, modify the password line in the configuration file.

    admin.password=YOURPASSWORD

Also, edit the server name property.

    server.name=YOUR.SERVER.NAME

If you plan on stopping and starting the server often (sometimes this is useful when testing an agent), set the start delay to zero.

    sim.startDelay=0

Unless you wish to examine the server logs (simulation logs provide game specific information), set to a high value (3 generates small logs).

    log.consoleLevel=3
    log.fileLevel=3

By default, the server uses ports:

* 6502 [agent connections]
* 8080 [http access]
* 4042 [viewer port]

These need to be distinct.

Optionally, set up mysql as the data store.

### Starting the server

Inside the aa-server directory, we provide a simple run script, named runServer.sh.

To start the server execute

    sh runServer.sh

