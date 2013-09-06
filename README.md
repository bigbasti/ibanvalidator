IBAN Validator
==============

So i was looking for a library which was able to validate and perform a structural check on an IBAN.
Unfortunately i couldn't find something that sadisfied my needs so i ended up implemening it my self.

The result is this relatively simple class. Feel free to use it in your own projets and to give me feedback.

Disclaimer
----------
I created it for usage in a project of mine so no warranty can be given. 
Also you will find some methods only relevant for german IBANs. They won't hurt you, so if you don't need them - don't use them :)

Usage
-----
The usage is pretty straight forward, there are basically 2 Methods you can call to
- Validate the IBAN (calculate the checknumber)
- Check the structure of the IBAN

###### IBAN Validation
Example:

```java
boolean valide;

try {
	String iban = "KZ86 125K-ZT50 0410 0100";
	valide = IBANValidator.validateIban(iban);
} catch (IBANValidationException e) {
	logger.error(e.getIBANValidationError().toString());
	valide = false;
}
```

Since all methods are static there is no need to initialize an IBANValidator object.
***
###### IBAN structure check
Every countrys IBAN is defined in an official document by swift. (You can fint it [here](http://www.swift.com/dsp/resources/documents/IBAN_Registry.pdf))
The structure check validates that the given IBAN matches that structure.

Right now you need to deliver the structure as a parameter to the function call. 
(Implementing the IBAN structure directory into the class is a fruture TODO for me.)

```java
boolean valid;
try {
	valid = IBANValidator.checkIbanStructure("DE89 3704 0044 0532-0130 00", "DE", "22", "DE2!n8!n10!n");
} catch (IBANValidationException e) {
	logger.error(e.getIBANValidationError().toString());
	valid = false;
}
```

Future plans
------------
- Since i don't need it right now i didn't implement a BBAN check, but it is definately on my TODO-List.
- The list of all IBAN structures (maintained by swift) is satatic and only updated every few years, so it would make sense to integrate the list into the class so you won't need to provide the IBAN structure yourself.
