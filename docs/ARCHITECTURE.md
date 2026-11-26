# Architektura a bezpečnost

## Tok dat

- Zdroj zaměstnanců: existující Excel na OneDrivu.
- Aplikace smí soubor pouze číst; nesmí zapisovat, přesouvat ani měnit jeho obsah.
- Pro přístup k OneDrivu se předpokládá Microsoft Graph a OAuth přihlášení uživatelky. Konkrétní oprávnění a souhlas firemního IT se musí ověřit.
- Zaměstnanecká data se nemají ukládat do GitHubu ani do externí databáze.
- Lokální cache, pokud bude nezbytná, musí mít omezenou dobu uchování a nesmí se synchronizovat do cloudu.

## Bezpečnost SMS

- Android aplikace musí explicitně ověřit dostupnost SIM ve slotu 2.
- Pokud telefon neumí bezpečně určit a vynutit SIM 2, rozesílání nebude povoleno.
- Při chybě, nejednoznačnosti nebo odebrání SIM se operace zastaví. Žádný fallback na SIM 1.
- Před odesláním zobrazit počet a seznam příjemců, text zprávy a potvrzovací krok.
- Zpočátku povolit pouze testovací režim s jedním předem nastaveným číslem.
- Ukládat výsledek každé zprávy (odesláno / chyba / nepotvrzeno), čas a počet příjemců. Obsah a osobní údaje historie musí být chráněny.

## Lokální spojení

Preferované řešení je lokální webová aplikace na PC a Android aplikace ve stejné důvěryhodné Wi-Fi síti. Párování zařízení musí používat jednorázový párovací kód a šifrované spojení. Telefon nesmí přijímat příkazy z internetu.

## Otevřené technické otázky

1. Přesný model OnePlus a verze Androidu.
2. Zda výrobce/Android zpřístupňuje API pro výběr konkrétní subscription/SIM a zda je SIM 2 stabilně identifikovatelná.
3. Zda firemní Microsoft 365 dovolí aplikaci přístup pouze pro čtení k určenému souboru.
4. Jaké jsou názvy sloupců pro jméno, telefon a středisko (stačí názvy sloupců, bez skutečných dat).

## Právní a organizační kontrola

Před ostrým použitím ověřit u odpovědných osob ve firmě právní titul, informační povinnost, pravidla používání služebního telefonu a interní bezpečnostní pravidla pro pracovní SMS.
