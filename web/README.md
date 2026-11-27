# Webové rozhraní KUVAG SMS

Jedno responzivní webové rozhraní pro počítač i mobilní prohlížeč. Počítač je primární pracovní prostředí; mobilní web je dostupná záloha. Samostatná Android aplikace zůstává odesílacím zařízením.

## Plánované funkce

- přihlášení uživatelky,
- stav spárovaného telefonu (online / offline),
- načtení zaměstnanců z existujícího Excelu na OneDrive přes Microsoft Graph pouze pro čtení,
- filtrování podle středisek a výběr jednotlivých zaměstnanců,
- příprava SMS, náhled příjemců a textu,
- předání šifrované úlohy telefonu přes schválený internetový relay,
- přehled výsledků odeslání a chráněná historie.

## Zásady

- Excel je jediný zdroj zaměstnaneckých údajů; žádná druhá databáze kontaktů.
- Do repozitáře nepatří skutečná jména, telefonní čísla, osobní čísla, exporty ani přístupové údaje.
- Web sám SMS neposílá. Odesílá výhradně Android companion po potvrzení na telefonu.
- Telefon vždy ověří služební T-Mobile SIM ve slotu 2; při nejasnosti se odesílání zastaví.
- Internetový relay zatím není vybrán ani nasazen. Před připojením reálných kontaktů musí firma schválit jeho provoz a tok dat.
- GitHub Pages, pokud bude použito, hostuje pouze statický frontend; není úložištěm zaměstnaneckých dat ani náhradou relay.

Viz [architektura a bezpečnost](../docs/ARCHITECTURE.md).
