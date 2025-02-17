# minotauro

A classification image tool set for personal dataset

<img src="minotaur.jpg" alt="minotaur" width="400"/>
---------

## Scopo generale, requisiti
Lo scopo di Minotauro è quello di classificare un dataset di immagini molto ampio, corrispondente all'archivio delle foto familiari degli ultimi 25-30 anni.
La classificazione ha come scopo quello di estrapolare le informazioni comuni a tutte le immagini e arricchirle quanto più possibile, incrociando informazioni presenti (titoli, exif, georeferenziazione, ecc.) con infomazioni dedotte (riconoscimento dei volti, dei luoghi, analisi dei titoli, ecc.).
Il risultato della classificazione deve corrispondere all'arricchimento di un database che consenta successivamente la ricerca rapida secondo dei filtri testuali.
Gli elementi che cascano all'interno del filtro devono essere documentati con un thumbnail sufficientemente grande in modo da poter valutare se è l'immagine voluta (oltre alle informazioni extra associate).

Particolarmente importante è la possibilità di selezionare le immagini di soggetti umani, mettendoli in corrispondenza con luoghi e date. 
Deve per questo essere possibile gestire anche dei dati associabili al soggetto (es. nome, età).

Inoltre l'ubicazione dell'immagine originale deve considerare la presenza di più dischi sorgente e/o CD,DVD. 

Meno rilevante ma utile potrebbe essere la localizzazione su mappa dei sotto dataset.

L'attività di processamento del dataset da parte di Minotauro si presume che avvenga rispetto a dispositivi di archiviazione locali alla macchina, ma anche in rete. Teoricamente, anche se sconsigliato per problemi di prestazioni, l'attività deve procedere anche in remoto accedendo direttamente al NAS dalla macchina client.
Inoltre essendo il processo comprensibilmente molto lungo, il progresso deve essere salvato immagine per immagine e lo stato di processamento corrente salvato, così da poter recuperare una sessione di lavoro interrotta in tempi rapidi.

Inizialmente non è prevista una GUI per l'operatività di Minotauro che potrebbe viceversa essere necessaria successivamente per la ricerca e la navigazione delle immagini.

## L'archivio
Attualmente l'archivio è salvato in un NAS su HD ridondati. Non è presente archiviazione dei contenuti che sono costituiti in prevalenza da immagini JPEG/RAW salvate all'interno di cartelle annidate secondo la seguente struttura:
ANNO / ANNO MESE TITOLO
es. ```<root archivio media>/2024/2024 12 Borgo Montello Natale/```

Insieme alle immagini originali, ci possono essere dei thumbnail, dei video (vario formato a seconda del dispositivo di acquisizione), dei file txt con alcuni dettagli, file di lavoro estranei come per esempio i modelli 3D derivati dalle immagini stesse o i certificati di calibrazione della fotocamera.
Le foto possono inoltre essere incluse anche in sotto cartelle come "lightroom" o "raw" ad indicare lo stato di elaborazione, il formato e l'eventuale ricampionamento. 
Immagini e video possono essere stati acquisiti con fotocamere diverse, es. Nikon D200, D50, D90... o Canon (foto di terzi), oppure con smartphone, o infine essere state scannerizzate da stampa.
Per questo non è sempre presente l'EXIF delle immagini e questo può avere contenuti non omogenei tra immagine e immagine, anche all'interno dello stesso contesto-cartella.

Il tema delle immagini è dei più vari e passa dai contesti di viaggio, alla vita familiare, alla sperimentazione fotografica. Vi si trovano quindi sia soggetti umani che inanimati e panorami. 
In alcuni casi la post elaborazione ha virato colori, passato l'immagine in bianco e nero, o applicato dei filtri artistici.
Un aiuto generico nella classificazione può venire dall'interpretazione del titolo della cartella contenitore, che fornisce in ogni caso un riferimento temporale e un contesto che associa le immagini contenute.

L'archivio attualmente incluso integralmente nel NAS è però suddiviso in 2 hard disk e questa condizione può essere estesa anche ad N hard disk dislocati geograficamente in posti diversi.
Occorre quindi prevedere la possibilità di nomi immagine identici all'interno di cartelle diverse.

Occorre eseguire una stima più precisa, ma i singoli file sono nell'ordine delle centinaia di migliaia, con una dimensione media di 4-6 MB a seconda della risoluzione e del dispositivo di acquisizione.

## Descrizione alto livello
Il software si compone di una serie di tool o procedure concatenate inserite all'interno di un flusso che si ipotizza essere il seguente:
- definizione di un file di configurazione con a titolo di esempio: dimensione thumbnail

## Componenti

