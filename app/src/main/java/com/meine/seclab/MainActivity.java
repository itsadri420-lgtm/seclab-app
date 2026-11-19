package com.meine.seclab;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends Activity {
    static final int BG = 0xFF0A0E1A, CARD = 0xFF131A2E, CYAN = 0xFF00E5FF, TXT = 0xFFE8F0FF,
            DIM = 0xFF8A97B8, GREEN = 0xFF4DFF88, RED = 0xFFFF5252, YEL = 0xFFFFD166;
    float dp;
    FrameWrap root;
    TextView termView;
    ScrollView termScroll;
    StringBuilder term = new StringBuilder();

    int d(int v) {
        return (int) (v * dp);
    }

    static class FrameWrap extends android.widget.FrameLayout {
        FrameWrap(Context c) {
            super(c);
        }
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        dp = getResources().getDisplayMetrics().density;
        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);
        root = new FrameWrap(this);
        root.setBackgroundColor(BG);
        showHome();
        setContentView(root);
    }

    // ---------- Hilfsfunktionen ----------
    TextView text(String s, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(size);
        t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT_BOLD);
        return t;
    }

    GradientDrawable box(int fill, int stroke, int radius) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(fill);
        g.setCornerRadius(d(radius));
        if (stroke != 0) g.setStroke(d(1), stroke);
        return g;
    }

    Button btn(String s, boolean primary) {
        Button b = new Button(this);
        b.setText(s);
        b.setAllCaps(false);
        b.setTypeface(Typeface.DEFAULT_BOLD);
        b.setStateListAnimator(null);
        if (primary) {
            b.setTextColor(0xFF07101F);
            b.setBackground(box(CYAN, 0, 12));
        } else {
            b.setTextColor(CYAN);
            b.setBackground(box(0x2200E5FF, CYAN, 12));
        }
        return b;
    }

    LinearLayout.LayoutParams lp(int top) {
        LinearLayout.LayoutParams l = new LinearLayout.LayoutParams(-1, -2);
        l.topMargin = d(top);
        return l;
    }

    LinearLayout card() {
        LinearLayout c = new LinearLayout(this);
        c.setOrientation(LinearLayout.VERTICAL);
        c.setBackground(box(CARD, 0x3300E5FF, 14));
        c.setPadding(d(16), d(14), d(16), d(14));
        return c;
    }

    void swap(View v) {
        root.removeAllViews();
        root.addView(v, new android.widget.FrameLayout.LayoutParams(-1, -1));
    }

    // ---------- Startseite ----------
    void showHome() {
        ScrollView sv = new ScrollView(this);
        LinearLayout col = new LinearLayout(this);
        col.setOrientation(LinearLayout.VERTICAL);
        col.setPadding(d(18), d(44), d(18), d(28));

        LinearLayout titleRow = new LinearLayout(this);
        titleRow.setOrientation(LinearLayout.HORIZONTAL);
        titleRow.setGravity(Gravity.CENTER_VERTICAL);
        TextView title = text("SecLab", 28, CYAN, true);
        title.setLetterSpacing(0.08f);
        LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(0, -2, 1f);
        titleRow.addView(title, tlp);
        Button info = btn("ⓘ Info", false);
        titleRow.addView(info, new LinearLayout.LayoutParams(-2, -2));
        col.addView(titleRow);
        col.addView(text("Lernsimulation für IT-Sicherheit · offline · fiktive Ziele", 13, DIM, false), lp(4));
        info.setOnClickListener(v -> showInfo());

        for (Lab l : LABS) {
            LinearLayout c = card();
            c.addView(text(l.icon + "  " + l.title, 18, TXT, true));
            c.addView(text(l.tagline, 13, DIM, false), lp(4));
            Button open = btn("Öffnen", true);
            c.addView(open, lp(10));
            open.setOnClickListener(v -> showLab(l));
            col.addView(c, lp(14));
        }
        sv.addView(col);
        swap(sv);
    }

    void showInfo() {
        new AlertDialog.Builder(this)
                .setTitle("Was diese App ist – und was nicht")
                .setMessage(
                        "WAS MÖGLICH IST:\n"
                                + "• Jede Kategorie hat ein fest einprogrammiertes, fiktives Ziel (Fake-Server, Fake-Login, Fake-Datenbank).\n"
                                + "• Du kannst eigene \"Skripte\" (einfache Listen/Texte) eingeben, die GEGEN DIESE FAKE-DATEN geprüft werden.\n"
                                + "• Das Terminal zeigt dir Schritt für Schritt, was dabei passiert, so wie ein echtes Tool es ausgeben würde.\n"
                                + "• Du lernst dadurch: was eine Technik grundsätzlich macht, welche Infos sie liefert, und wie man mit diesen Infos weiterarbeitet.\n\n"
                                + "WAS NICHT MÖGLICH IST:\n"
                                + "• Diese App hat KEINEN Internet- oder Netzwerkzugriff.\n"
                                + "• Es gibt KEINE echten Angriffstechniken oder Exploit-Codes in der App.\n"
                                + "• Nichts hier funktioniert gegen echte Webseiten, WLANs oder Accounts.\n"
                                + "• Die \"Skripte\" sind vereinfachte Mustererkennung auf Fake-Daten, keine echte Sicherheitslücken-Ausnutzung.\n\n"
                                + "Für echtes, legales Üben an realen (aber eigens dafür freigegebenen) Zielen: TryHackMe, Hack The Box, PortSwigger Web Security Academy, OWASP Juice Shop.")
                .setPositiveButton("Verstanden", null)
                .show();
    }

    // ---------- Labore ----------
    static class Lab {
        String id, icon, title, tagline, explain, target, hint;
    }

    static final Lab[] LABS = new Lab[4];

    static {
        LABS[0] = new Lab();
        LABS[0].id = "scan";
        LABS[0].icon = "📡";
        LABS[0].title = "Port-Scan (wie Nmap)";
        LABS[0].tagline = "Welche Dienste laufen auf einem Server?";
        LABS[0].explain = "Ein Portscan sendet Anfragen an verschiedene Ports eines Zielrechners und prüft, welche antworten. "
                + "Offene Ports zeigen, welche Dienste laufen (z. B. 80 = Webserver, 22 = Fernzugriff, 21 = Dateiübertragung). "
                + "Diese Infos nutzt man, um zu verstehen, welche Angriffsfläche ein System hat, und welche Dienste man absichern sollte.\n\n"
                + "Dein \"Skript\": Gib eine Liste von Ports ein, getrennt mit Komma (z. B. 21,22,80,443,8080). "
                + "Die App prüft sie gegen das fiktive Zielsystem und zeigt dir im Terminal, welche davon offen sind.";
        LABS[0].target = "Fiktiver Server 10.0.0.42 — offene Ports: 22, 80, 3306";
        LABS[0].hint = "Portliste, z. B. 21,22,23,80,443,3306,8080";

        LABS[1] = new Lab();
        LABS[1].id = "brute";
        LABS[1].icon = "🔑";
        LABS[1].title = "Brute-Force (Passwort)";
        LABS[1].tagline = "Wie schwach ist ein Passwort wirklich?";
        LABS[1].explain = "Bei Brute-Force probiert man systematisch Passwörter aus einer Liste durch, bis eines passt. "
                + "Das zeigt, wie wichtig lange, einzigartige Passwörter und Begrenzungen für Login-Versuche sind. "
                + "Die gewonnene Info: welche Passwörter zu schwach sind und wie schnell sie gefunden werden.\n\n"
                + "Dein \"Skript\": Gib eine Wortliste ein, getrennt mit Komma (z. B. admin,1234,sommer2024,passwort). "
                + "Die App prüft jedes Wort gegen das fiktive Passwort und zeigt die Versuche im Terminal.";
        LABS[1].target = "Fiktiver Login — Benutzer: admin, Passwort unbekannt (im Terminal nicht sichtbar)";
        LABS[1].hint = "Wortliste, z. B. admin,1234,passwort,sommer2024,qwertz";

        LABS[2] = new Lab();
        LABS[2].id = "sqli";
        LABS[2].icon = "🗄️";
        LABS[2].title = "SQL-Injection";
        LABS[2].tagline = "Was passiert, wenn Eingaben nicht geprüft werden?";
        LABS[2].explain = "SQL-Injection nutzt aus, dass eine Anwendung Benutzereingaben ungeprüft in eine Datenbank-Abfrage einbaut. "
                + "Dadurch kann man die Logik der Abfrage verändern, z. B. eine Login-Prüfung umgehen. "
                + "Die Lektion: Eingaben müssen immer serverseitig geprüft und sauber getrennt von Befehlen behandelt werden (z. B. mit \"prepared statements\").\n\n"
                + "Dein \"Skript\": Gib einen Text fürs Login-Feld ein. Die App prüft nur, ob dein Text eines von drei bekannten, "
                + "stark vereinfachten Mustern (z. B. Apostroph + \"OR\") enthält, und erklärt dir im Terminal, warum das in einer "
                + "echten, unsicheren Anwendung die Abfrage verändern würde. Es handelt sich um reine Mustererkennung auf Fake-Daten, keine echte Datenbank-Ausführung.";
        LABS[2].target = "Fiktives Login-Formular einer (nicht existierenden) Beispiel-Webseite";
        LABS[2].hint = "Text fürs Login-Feld, z. B. ' OR '1'='1";

        LABS[3] = new Lab();
        LABS[3].id = "xss";
        LABS[3].icon = "💬";
        LABS[3].title = "XSS (Cross-Site-Scripting)";
        LABS[3].tagline = "Was passiert, wenn Kommentare ungefiltert angezeigt werden?";
        LABS[3].explain = "XSS nutzt aus, dass eine Webseite Benutzereingaben ungeprüft wieder anzeigt, z. B. in einem Kommentarfeld. "
                + "Enthält die Eingabe Code, kann er im Browser anderer Besucher ausgeführt werden. "
                + "Die Lektion: Eingaben müssen beim Anzeigen immer sauber kodiert (escaped) werden.\n\n"
                + "Dein \"Skript\": Gib einen Text fürs Kommentarfeld ein. Die App prüft nur, ob er wie ein Skript-Tag aussieht "
                + "(enthält z. B. \"<script\"), und zeigt im Terminal, ob das fiktive Kommentarsystem ihn \"ausführen\" würde.";
        LABS[3].target = "Fiktives Kommentarfeld einer (nicht existierenden) Beispiel-Webseite";
        LABS[3].hint = "Kommentartext, z. B. <script>alert(1)</script>";
    }

    // ---------- Lab-Ansicht ----------
    EditText input;
    TextView targetView;

    void showLab(Lab l) {
        term.setLength(0);
        logLine("# SecLab Terminal — " + l.title, DIM);
        logLine("# Alle Ziele sind fiktiv. Kein Netzwerkzugriff.", DIM);

        ScrollView sv = new ScrollView(this);
        LinearLayout col = new LinearLayout(this);
        col.setOrientation(LinearLayout.VERTICAL);
        col.setPadding(d(18), d(44), d(18), d(28));

        LinearLayout titleRow = new LinearLayout(this);
        titleRow.setOrientation(LinearLayout.HORIZONTAL);
        titleRow.setGravity(Gravity.CENTER_VERTICAL);
        Button back = btn("← Zurück", false);
        titleRow.addView(back);
        TextView t = text(l.icon + " " + l.title, 19, CYAN, true);
        LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(0, -2, 1f);
        tlp.leftMargin = d(10);
        titleRow.addView(t, tlp);
        col.addView(titleRow);
        back.setOnClickListener(v -> showHome());

        LinearLayout ec = card();
        ec.addView(text("Erklärung", 15, TXT, true));
        ec.addView(text(l.explain, 13, DIM, false), lp(8));
        col.addView(ec, lp(16));

        LinearLayout gc = card();
        gc.addView(text("Fiktives Ziel", 15, TXT, true));
        gc.addView(text(l.target, 13, GREEN, false), lp(6));
        col.addView(gc, lp(14));

        LinearLayout ic = card();
        ic.addView(text("Dein Skript", 15, TXT, true));
        input = new EditText(this);
        input.setHint(l.hint);
        input.setHintTextColor(DIM);
        input.setTextColor(TXT);
        input.setBackground(box(0x22FFFFFF, 0x3300E5FF, 10));
        input.setPadding(d(12), d(10), d(12), d(10));
        input.setMinLines(2);
        input.setGravity(Gravity.TOP);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        ic.addView(input, lp(8));
        Button run = btn("▶ Ausführen (simuliert)", true);
        ic.addView(run, lp(10));
        col.addView(ic, lp(14));
        run.setOnClickListener(v -> runLab(l));

        LinearLayout tc = card();
        tc.addView(text("Terminal", 15, TXT, true));
        termScroll = new ScrollView(this);
        termScroll.setBackground(box(0xFF05070E, 0x33FFFFFF, 10));
        termScroll.setPadding(d(10), d(10), d(10), d(10));
        termView = text("", 11, GREEN, false);
        termView.setTypeface(Typeface.MONOSPACE);
        termScroll.addView(termView);
        LinearLayout.LayoutParams sclp = lp(8);
        sclp.height = d(220);
        tc.addView(termScroll, sclp);
        col.addView(tc, lp(14));
        renderTerm();

        sv.addView(col);
        swap(sv);
    }

    void logLine(String s, int color) {
        String hex = String.format("#%06X", (0xFFFFFF & color));
        term.append("<font color='").append(hex).append("'>").append(escape(s)).append("</font><br>");
    }

    static String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    void renderTerm() {
        termView.setText(android.text.Html.fromHtml(term.toString(), android.text.Html.FROM_HTML_MODE_LEGACY));
        termScroll.post(() -> termScroll.fullScroll(View.FOCUS_DOWN));
    }

    void runLab(Lab l) {
        String val = input.getText().toString().trim();
        logLine("", TXT);
        logLine("$ ausführen: " + l.id + " --input \"" + val + "\"", CYAN);
        if (val.isEmpty()) {
            logLine("Fehler: kein Skript eingegeben.", RED);
            renderTerm();
            return;
        }
        switch (l.id) {
            case "scan":
                runScan(val);
                break;
            case "brute":
                runBrute(val);
                break;
            case "sqli":
                runSqli(val);
                break;
            case "xss":
                runXss(val);
                break;
        }
        renderTerm();
    }

    void runScan(String val) {
        String[] ports = val.split(",");
        int[] openPorts = {22, 80, 3306};
        logLine("Verbinde zu fiktivem Host 10.0.0.42 (simuliert)…", DIM);
        int found = 0;
        for (String raw : ports) {
            String s = raw.trim();
            if (s.isEmpty()) continue;
            int p;
            try {
                p = Integer.parseInt(s);
            } catch (Exception e) {
                logLine("Port " + s + ": ungültig, übersprungen", YEL);
                continue;
            }
            boolean open = false;
            for (int op : openPorts) if (op == p) open = true;
            if (open) {
                found++;
                String svc = p == 22 ? "SSH" : p == 80 ? "HTTP" : p == 3306 ? "MySQL" : "?";
                logLine("Port " + p + "/tcp  OPEN   (" + svc + ")", GREEN);
            } else {
                logLine("Port " + p + "/tcp  closed", DIM);
            }
        }
        logLine("", TXT);
        if (found > 0) {
            logLine(found + " offene(r) Port(s) gefunden.", GREEN);
            logLine("Bedeutung: Diese Dienste sind von außen ansprechbar und sollten gezielt abgesichert werden "
                    + "(aktuelle Version, Zugriffsbeschränkung, starke Zugangsdaten).", TXT);
        } else {
            logLine("Keine der angegebenen Ports sind auf diesem fiktiven System offen.", YEL);
        }
    }

    void runBrute(String val) {
        String[] words = val.split(",");
        String secret = "s0nne2024!";
        logLine("Starte Wortlisten-Angriff gegen fiktiven Login 'admin' (simuliert)…", DIM);
        boolean hit = false;
        int tries = 0;
        for (String raw : words) {
            String w = raw.trim();
            if (w.isEmpty()) continue;
            tries++;
            if (w.equals(secret)) {
                logLine("Versuch " + tries + ": \"" + w + "\"  →  TREFFER", GREEN);
                hit = true;
                break;
            } else {
                logLine("Versuch " + tries + ": \"" + w + "\"  →  falsch", DIM);
            }
        }
        logLine("", TXT);
        if (hit) {
            logLine("Passwort nach " + tries + " Versuchen gefunden.", GREEN);
            logLine("Bedeutung: Ein Login ohne Versuchsbegrenzung oder mit vorhersehbaren Passwörtern "
                    + "ist durch einfache Wortlisten angreifbar. Schutz: Rate-Limiting, Konto-Sperren, lange zufällige Passwörter, 2FA.", TXT);
        } else {
            logLine(tries + " Wörter probiert, kein Treffer.", YEL);
            logLine("Das fiktive Passwort war nicht in deiner Liste enthalten.", DIM);
        }
    }

    void runSqli(String val) {
        logLine("Sende Eingabe an fiktives Login-Formular (simuliert)…", DIM);
        String low = val.toLowerCase(Locale.US);
        boolean p1 = low.contains("'") && low.contains("or");
        boolean p2 = low.contains("--") || low.contains("#");
        boolean p3 = low.contains("union") && low.contains("select");
        if (p1 || p2 || p3) {
            logLine("Simulierte Abfrage würde sich so verändern:", YEL);
            logLine("  SELECT * FROM users WHERE user='admin' AND pass='" + val + "'", DIM);
            logLine("Muster erkannt: " + (p1 ? "OR-Bedingung mit Apostroph " : "")
                    + (p2 ? "Kommentar-Zeichen " : "") + (p3 ? "UNION SELECT " : ""), GREEN);
            logLine("", TXT);
            logLine("Bedeutung: In einer unsich
