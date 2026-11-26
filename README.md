# KUVAG SMS

Soukromý projekt pro interní rozesílání pracovních SMS zaměstnancům KUVAG.

## Cíl

Desktopové rozhraní pro výběr příjemců podle zaměstnanců a středisek. Kontakty se budou pouze číst z existujícího zaměstnaneckého Excelu na OneDrivu. SMS bude odesílat doprovodná aplikace na vlastním telefonu Android přes výslovně zvolenou služební SIM ve slotu 2.

## Bezpečnostní zásady

- Do tohoto repozitáře nikdy nevkládat zaměstnanecká jména, telefonní čísla, osobní čísla, exporty Excelu, přístupové tokeny ani tajné klíče.
- Excel zůstává jediným zdrojem zaměstnaneckých údajů; aplikace jej nesmí upravovat.
- Žádná externí SMS brána ani databáze zaměstnanců.
- Odesílání musí být zastaveno, pokud nelze jednoznačně ověřit služební SIM ve slotu 2. Nikdy nepřepnout na SIM 1.
- Před každou skutečnou rozesílkou zobrazit souhrn příjemců a vyžadovat potvrzení.
- Historii odesílání ukládat lokálně a přístup k ní chránit.

## Navržená architektura

1. **Webové rozhraní** běží lokálně na počítači a slouží k výběru příjemců, psaní zpráv a kontrole rozesílky.
2. **Čtení Excelu** probíhá přes Microsoft Graph s minimálními oprávněními a pouze pro čtení. Přístup bude nutné ověřit s firemním IT.
3. **Android aplikace** v telefonu přijímá pokyny z lokální sítě a odesílá SMS přes explicitně vybranou SIM 2.
4. **Propojení PC–telefon** bude lokální a zabezpečené; žádný veřejný server ani SMS poskytovatel.

Tato architektura je návrh, nikoli potvrzení kompatibility telefonu. Výběr SIM 2 a oprávnění Androidu ověříme na konkrétním modelu ještě před implementací odesílání.

## Stav

- [x] Soukromý GitHub repozitář založen
- [x] Bezpečnostní zásady a architektura sepsány
- [ ] Ověřit přesný model OnePlus a verzi Androidu
- [ ] Ověřit možnost vynuceného odesílání přes SIM 2
- [ ] Zjistit názvy sloupců v Excelu (bez sdílení reálných osobních údajů)
- [ ] Ověřit možnosti Microsoft Graph a firemní IT souhlas
- [ ] Vytvořit lokální webové rozhraní
- [ ] Vytvořit Android doprovodnou aplikaci
- [ ] Otestovat nejprve pouze s vlastním testovacím číslem

## Vývoj

Projekt budeme připravovat přes GitHub Codespaces v prohlížeči, bez nutnosti instalovat vývojové prostředí na počítač. Instalace doprovodné aplikace do telefonu bude nutná.


## Ověření telefonu: OnePlus Nord CE5

Oficiální specifikace OnePlus potvrzují dva sloty pro nano-SIM (hybridní zásuvka: druhá pozice může být alternativně využita pro microSD). Telefon tedy hardwarově podporuje dvě SIM, pokud jsou skutečně vloženy dvě SIM karty.

Na Androidu lze postupovat bezpečněji než přes výchozí SIM:
- načíst aktivní předplatná SIM,
- zjistit slot index každého předplatného,
- vybrat pouze předplatné se slot indexem **1** (Android čísluje sloty od nuly, takže index 1 odpovídá druhému slotu),
- vytvořit SmsManager výslovně pro toto subscription ID pomocí createForSubscriptionId,
- pokud slot 1 nebo jeho subscription ID chybí, je neplatné nebo se nedá ověřit, odeslání zablokovat.

Tohle je podporovaný postup Android API, nikoli ještě prakticky ověřená záruka pro konkrétní firmware OnePlus. Před skutečným použitím uděláme diagnostický test, který pouze zobrazí nalezené SIM a jejich sloty. Teprve po potvrzení správné služební SIM připravíme testovací SMS na vlastní číslo.

Zdroje:
- [Oficiální specifikace OnePlus Nord CE5](https://www.oneplus.com/mt/nord-ce5/specs)
- [Android SubscriptionManager](https://developer.android.com/reference/android/telephony/SubscriptionManager)
- [Android SmsManager](https://developer.android.com/reference/android/telephony/SmsManager)
