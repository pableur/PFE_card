#!/usr/bin/env python
# -*- coding: utf-8 -*-

from sgmllib import SGMLParser
from HTMLParser import HTMLParser
import urllib2
from BDD import *

#"http://magic.wizards.com/node/46383"
url = "file:///E:/programmation/PFE_card/Card%20Set%20Archive%20_%20MAGIC%20%20THE%20GATHERING.htm"

bdd=Bdd()

class MyHTMLParser(HTMLParser):

    def __init__(self):
        HTMLParser.__init__(self)
        self.attribut={"title","nameSet", "quantity","date-display-single", 'logo', 'icon'}
        self.editionDictionnaire={}
        self.triggerDictionnaire={}
        self.allBloc=[]
        self.allEdition=[{}]
        self.iteration=0
    
        # initialise des variables
        self.startParser=False
        for i in self.attribut:
            self.editionDictionnaire[i]=""
            self.triggerDictionnaire[i]=False
	
    def handle_starttag(self, tag, attrs):
            
        #declanche le triger du nom du bloc
        if tag=='li' and len(attrs)>0:		
            if attrs[0][0]=='class':
                if attrs[0][1]=='title':
                    self.triggerDictionnaire['title']=True
        if tag=='img':
            temp =attrs[0]
            if temp[1].find('Logo')>0:
                self.editionDictionnaire['logo']=temp[1]
            if temp[1].find('SYMBOL')>0 or temp[1].find('Symbol')>0 or temp[1].find('Icon')>0 :
                self.editionDictionnaire['icon']=temp[1]
                    
        if tag=='span' and len(attrs)>0:		
            if attrs[0][0]=='class':
                for i in self.attribut:
                    if attrs[0][1]==i:
                        self.triggerDictionnaire[i]=True
                    if attrs[0][1]=="date-display-single":
                        for j in attrs :
                            if j[0]=="content":
                                self.dateEdition=j[1]     
                                #date au format Year Month Day
                                self.dateEdition=self.dateEdition.split('T')[0]
                        
                        

    def handle_endtag(self, tag):
         pass

    def handle_data(self, data):
        if self.triggerDictionnaire['title']==True:            
            self.editionDictionnaire['title']=data.strip()               
            self.triggerDictionnaire['title']=False
            #print "Bloc courant : "+self.editionDictionnaire['title']
            if len(data.strip()):
                self.allBloc.append(self.editionDictionnaire['title'])
                
        else:
            for i in self.attribut:
                if self.triggerDictionnaire[i]==True:
                    self.triggerDictionnaire[i]=False                                                   
                    
                    if i=="date-display-single":
                        self.editionDictionnaire[i]=self.dateEdition.encode("utf-8")
                        # comme on est a la fin de la ligne, enregistre les infos 
                        self.displayName()
                        if 'bdd' in globals():
                            idBloc=bdd.addBloc(self.editionDictionnaire['title'])
                            bdd.addEdition(self.editionDictionnaire['nameSet'],
                                        idBloc,
                                        self.editionDictionnaire['date-display-single'],
                                        self.editionDictionnaire['logo'],
                                        self.editionDictionnaire['icon'])

                    else:
                        #strip pour éliminer les espace et encode pour éliminer l'erreur sur les caracteres
                        self.editionDictionnaire[i]=self.convert(data.strip())
                        self.editionDictionnaire[i]=self.editionDictionnaire[i].encode("utf-8") 
                        
    def displayEdition(self):
        print " "
        print "------------------------------------"
        for j in self.attribut: 
            print j+ ":",
            if len(self.editionDictionnaire[j])>0:
                print self.editionDictionnaire[j]
            else:
                print "None "
        print ""
    def displayName(self):
        print self.editionDictionnaire['nameSet']
        
    # retranscrit les caractères sur 7 bites
    def convert(self, string):
        returnString=""
        for i in string: 
            if ord(i)>127:
                if ord(i)==226:
                    returnString=returnString+"'"                
            else:
                returnString=returnString+i
        
        return returnString

                        
if __name__ == "__main__":
    print "//////////////////////////////////////////////"
    print "///	    Enregistre les editions 	  ///"
    print "//////////////////////////////////////////////"
    usock = urllib2.urlopen(url)
		
    temp = usock.read()
    html = temp.decode('ISO-8859-1')
    usock.close()
    parser = MyHTMLParser()
    parser.feed(html)

    