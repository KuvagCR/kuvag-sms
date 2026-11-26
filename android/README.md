# Android companion

Doprovodná aplikace pro vlastní telefon Android. Zatím neodesílá žádné SMS.

Před implementací je nutné ověřit přesný model telefonu a verzi Androidu. Bezpečnostní podmínka: aplikace smí odeslat SMS pouze přes explicitně vybranou služební SIM ve slotu 2. Pokud to nelze spolehlivě vynutit a ověřit, musí odeslání odmítnout.

Oprávnění SEND_SMS bude vyžadovat výslovné udělení v telefonu. Aplikace bude určena k vlastní instalaci, nikoli k publikaci v obchodě.
