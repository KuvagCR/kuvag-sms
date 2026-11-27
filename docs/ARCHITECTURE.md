# Architektura KUVAG SMS

## Cíl

Jedna responzivní webová aplikace pro počítač i mobilní prohlížeč. Počítač je hlavní pracovní rozhraní; mobilní web slouží jako plnohodnotná záložní možnost. Samostatná Android aplikace v telefonu zajišťuje vlastní odeslání SMS přes služební SIM.

## Komponenty

1. **Webové rozhraní** – statická/responzivní aplikace dostupná přes HTTPS. Na PC i mobilu používá stejné funkce a účet.
2. **Microsoft 365 / OneDrive Excel** – jediný zdroj zaměstnanců, telefonních čísel a středisek. Web načítá určený soubor přes Microsoft Graph s oprávněním pouze pro čtení. Žádná druhá evidence zaměstnanců.
3. **Zabezpečený předávací server (relay)** – propojí web a Android telefon i v různých sítích. Není to SMS brána ani databáze kontaktů. Má uchovávat jen krátkodobé šifrované úlohy a technický stav, s automatickým mazáním.
4. **Android companion** – připojuje se k relay, ověřuje SIM a zobrazí obsah úlohy. SMS odešle pouze po samostatném potvrzení přímo na telefonu.

## Tok práce

1. Uživatelka se přihlásí do webu a načte aktuální kontakty z firemního Excelu.
2. Vybere zaměstnance, střediska nebo jednotlivé osoby a připraví text.
3. Web ukáže počet a seznam příjemců i přesný text ke kontrole.
4. Po potvrzení předá úlohu zabezpečeným způsobem přes relay telefonu.
5. Telefon úlohu ověří, zobrazí příjemce a text a vyžádá si výslovné potvrzení.
6. Android znovu ověří služební SIM T-Mobile ve slotu 2 a oprávnění. Při jakékoli nejasnosti odesílání zastaví.
7. Telefon odešle SMS výhradně přes subscription ID ověřené SIM ve slotu 2 a vrátí stav jednotlivých zpráv.

## Bezpečnost a ochrana údajů

- Žádná zaměstnanecká data, telefonní čísla, exporty Excelu, tokeny ani klíče do GitHubu.
- Excel zůstává jediným zdrojem údajů; aplikace do něj nezapisuje.
- Microsoft Graph musí mít nejmenší nutné oprávnění, ideálně pouze ke konkrétnímu souboru, pokud to firemní prostředí umožní. Přístup a souhlas ověří firemní IT.
- Relay nesmí dostávat čitelná telefonní čísla ani texty zpráv. Obsah úloh musí být šifrován mezi webem a spárovaným telefonem (end-to-end); server zajišťuje pouze doručení ciphertextu.
- Párování zařízení přes jednorázový kód, krátkou platnost, vzájemné ověření a možnost zrušení párování.
- Krátká životnost úloh, ochrana proti opakovanému odeslání (jedinečné ID úlohy a idempotence), auditní technické záznamy bez zbytečných osobních údajů.
- HTTPS všude, žádná veřejná příchozí služba na telefonu a žádné příkazy k odeslání bez potvrzení na telefonu.
- Mobilní i desktopový web mají stejné přihlašovací a autorizační požadavky.
- Historie odesílání má být omezená a chráněná; její rozsah a dobu uchování je nutné stanovit s firmou.
- Před ostrým provozem ověřit právní titul, informační povinnost, pravidla služebního telefonu a interní bezpečnostní pravidla.

## Zásadní omezení a otevřené rozhodnutí

Vzdálené propojení PC a telefonu bez stejné Wi-Fi vyžaduje server dostupný z internetu. I při end-to-end šifrování opouští šifrovaný obsah zařízení a prochází infrastrukturou relay. Proto před nasazením skutečných kontaktů musí KUVAG schválit umístění a provoz relay, správu klíčů, dobu uchování a poskytovatele. Zatím není vybrán ani nasazen žádný hosting.

GitHub Pages může případně hostovat pouze statické soubory webového rozhraní; nesmí obsahovat zaměstnanecká data ani tajné údaje. Samotné Pages nenahrazuje relay ani bezpečné přihlášení.

## Postup implementace

1. Responzivní webový prototyp s fiktivními ukázkovými údaji.
2. Přihlášení Microsoft a bezpečné načtení konkrétního Excelu pouze pro čtení.
3. Návrh a schválení relay (bez skutečných dat do schválení).
4. Párování telefonu a ověření online/offline stavu.
5. Předávání šifrovaných testovacích úloh.
6. Test odeslání pouze na vlastní číslo, potvrzený na telefonu.
7. Teprve po schválení firmy pilotní provoz s omezeným počtem příjemců.

## Ověřené chování zařízení

Na OnePlus Nord CE5 s Androidem 16 bylo prakticky ověřeno:
- soukromá SIM je ve slotu 1,
- firemní T-Mobile SIM je ve slotu 2 (Android slot index 1),
- aplikace získala oprávnění SMS a úspěšně předala testovací SMS přes služební SIM.

Android aplikace musí při každém odeslání znovu ověřit právě jednu aktivní SIM ve slotu index 1 a operátora T-Mobile. Nikdy nesmí použít výchozí SMS manager ani přepnout na soukromou SIM.
