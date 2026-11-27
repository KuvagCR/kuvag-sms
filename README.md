# KUVAG SMS

Soukromý projekt pro interní rozesílání pracovních SMS zaměstnancům KUVAG.

## Cíl

Jedna responzivní webová aplikace pro počítač i mobil. Počítač je hlavní pracovní prostředí, mobilní web je záložní možnost. Kontakty se načítají pouze pro čtení z existujícího zaměstnaneckého Excelu na OneDrivu. Samostatná Android aplikace v telefonu odesílá SMS výhradně přes ověřenou služební SIM ve slotu 2.

## Zásady

- Excel zůstává jediným zdrojem zaměstnaneckých údajů; aplikace jej nesmí upravovat.
- Do GitHubu nikdy nevkládat zaměstnanecká jména, telefonní čísla, osobní čísla, exporty Excelu, tokeny ani tajné klíče.
- Žádná externí SMS brána ani databáze zaměstnanců.
- Odesílání se zastaví, pokud nelze jednoznačně ověřit služební T-Mobile SIM ve slotu 2. Nikdy nepřepnout na SIM 1.
- Každá rozesílka vyžaduje kontrolu příjemců a textu a samostatné potvrzení přímo na telefonu.
- Webové rozhraní bude stejné na počítači i mobilu; telefon zůstává odesílacím zařízením.

## Architektura

Web načítá kontakty přes Microsoft Graph pouze pro čtení. Pro propojení počítače a telefonu v různých sítích je nutný internetový relay. Obsah zpráv a příjemců musí být šifrován mezi webem a spárovaným telefonem; relay má přenášet pouze ciphertext a krátkodobá technická metadata. Poskytovatel a umístění relay zatím nejsou vybrány. Před použitím skutečných zaměstnaneckých údajů je nutné schválení KUVAG a firemního IT.

Podrobnosti: [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).

## Aktuální stav

- [x] Soukromý repozitář a zásady ochrany dat
- [x] Android aplikace ověřuje T-Mobile SIM ve slotu 2
- [x] Na OnePlus Nord CE5 / Android 16 úspěšně povoleno oprávnění SMS a odeslán test na vlastní číslo
- [ ] Responzivní webové rozhraní
- [ ] Přihlášení Microsoft a čtení určeného Excelu přes Graph
- [ ] Schválení a nasazení internetového relay
- [ ] Párování webu s telefonem a šifrované předávání úloh
- [ ] Pilotní test s vlastními čísly

## Vývoj

Projekt zůstává v tomto repozitáři. Vývoj probíhá přes GitHub v prohlížeči; na firemní počítač není potřeba instalovat vývojové prostředí. Instalace doprovodné aplikace do telefonu je nutná.

GitHub Pages deployment test.
