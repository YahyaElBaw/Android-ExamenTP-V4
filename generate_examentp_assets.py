import os
from playwright.sync_api import sync_playwright

SCREENSHOTS_DIR = os.path.abspath(r"D:\L3MDW\Mobile\ExamenTP\screenshots")
os.makedirs(SCREENSHOTS_DIR, exist_ok=True)

def get_v4_list_html(font_size=16, font_color="#0F172A", show_call_buttons=False):
    contacts = [
        ("Bernard Sophie", "06 98 76 54 32"),
        ("Dupont Claire", "06 12 34 56 78"),
        ("Martin Thomas", "07 23 45 67 89"),
        ("Moreau Julien", "06 55 44 33 22"),
        ("Petit Lucas", "07 11 22 33 44")
    ]
    items_html = ""
    for name, tel in contacts:
        call_btn_html = f"""
        <div style="width: 36px; height: 36px; border-radius: 18px; background: #10B981; display: flex; align-items: center; justify-content: center; color: white; margin-right: 8px; font-size: 16px;">
            📞
        </div>
        """ if show_call_buttons else ""

        items_html += f"""
        <div style="background: white; padding: 12px 16px; border-bottom: 1px solid #E2E8F0; display: flex; align-items: center; justify-content: space-between;">
            <div style="display: flex; align-items: center; gap: 12px;">
                <div style="width: 44px; height: 44px; border-radius: 22px; background: #E2E8F0; display: flex; align-items: center; justify-content: center; font-size: 20px;">👤</div>
                <div>
                    <div style="font-size: {font_size}px; font-weight: bold; color: {font_color};">{name}</div>
                    <div style="font-size: {max(12, font_size - 2)}px; color: #64748B; margin-top: 2px;">{tel}</div>
                </div>
            </div>
            <div style="display: flex; align-items: center;">
                {call_btn_html}
                <div style="color: #94A3B8; font-size: 16px;">›</div>
            </div>
        </div>
        """

    return f"""
    <!DOCTYPE html>
    <html>
    <head><meta charset="utf-8">
    <style>
        * {{ box-sizing: border-box; margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; }}
        body {{ background: transparent; display: flex; justify-content: center; align-items: center; padding: 20px; }}
        .phone-frame {{ width: 340px; height: 680px; background: #F8FAFC; border-radius: 36px; border: 9px solid #1E293B; box-shadow: 0 20px 40px rgba(0,0,0,0.25); display: flex; flex-direction: column; position: relative; overflow: hidden; }}
        .status-bar {{ height: 28px; background: #0F172A; display: flex; justify-content: space-between; align-items: center; padding: 0 16px; color: white; font-size: 11px; font-weight: 600; }}
        .app-bar {{ height: 56px; background: #1E293B; color: white; display: flex; align-items: center; justify-content: space-between; padding: 0 16px; font-size: 18px; font-weight: bold; }}
        .fab {{ position: absolute; bottom: 36px; right: 20px; width: 56px; height: 56px; border-radius: 28px; background: #3B82F6; color: white; display: flex; align-items: center; justify-content: center; font-size: 28px; box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4); z-index: 50; }}
        .nav-bar {{ height: 22px; background: #F8FAFC; display: flex; justify-content: center; align-items: center; }}
        .home-indicator {{ width: 90px; height: 4px; background: #94A3B8; border-radius: 2px; }}
    </style>
    </head>
    <body>
        <div class="phone-frame">
            <div class="status-bar"><span>12:00</span><span>📶 🔋 100%</span></div>
            <div class="app-bar">
                <span>Mes contacts (SQLite)</span>
                <span style="font-size: 20px; cursor: pointer;">⚙️</span>
            </div>
            <div style="margin: 12px; background: white; border-radius: 8px; border: 1px solid #CBD5E1; height: 42px; display: flex; align-items: center; padding: 0 12px; gap: 8px;">
                <span style="color: #64748B;">🔍</span>
                <span style="color: #94A3B8; font-size: 13px;">Rechercher un contact...</span>
            </div>
            <div style="flex: 1; overflow-y: auto;">{items_html}</div>
            <div class="fab">+</div>
            <div class="nav-bar"><div class="home-indicator"></div></div>
        </div>
    </body>
    </html>
    """

def get_v4_settings_html():
    return f"""
    <!DOCTYPE html>
    <html>
    <head><meta charset="utf-8">
    <style>
        * {{ box-sizing: border-box; margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; }}
        body {{ background: transparent; display: flex; justify-content: center; align-items: center; padding: 20px; }}
        .phone-frame {{ width: 340px; height: 680px; background: #F8FAFC; border-radius: 36px; border: 9px solid #1E293B; box-shadow: 0 20px 40px rgba(0,0,0,0.25); display: flex; flex-direction: column; position: relative; overflow: hidden; }}
        .status-bar {{ height: 28px; background: #0F172A; display: flex; justify-content: space-between; align-items: center; padding: 0 16px; color: white; font-size: 11px; font-weight: 600; }}
        .app-bar {{ height: 56px; background: #1E293B; color: white; display: flex; align-items: center; padding: 0 16px; font-size: 18px; font-weight: bold; gap: 12px; }}
        .category-title {{ padding: 14px 16px 6px 16px; font-size: 12px; font-weight: bold; color: #3B82F6; text-transform: uppercase; }}
        .pref-item {{ background: white; padding: 14px 16px; border-bottom: 1px solid #E2E8F0; display: flex; justify-content: space-between; align-items: center; }}
        .pref-title {{ font-size: 15px; font-weight: 600; color: #0F172A; }}
        .pref-summary {{ font-size: 12px; color: #64748B; margin-top: 2px; }}
        .switch-on {{ width: 42px; height: 24px; border-radius: 12px; background: #3B82F6; display: flex; align-items: center; justify-content: flex-end; padding: 2px; }}
        .switch-thumb {{ width: 20px; height: 20px; border-radius: 10px; background: white; }}
        .nav-bar {{ height: 22px; background: #F8FAFC; display: flex; justify-content: center; align-items: center; }}
        .home-indicator {{ width: 90px; height: 4px; background: #94A3B8; border-radius: 2px; }}
    </style>
    </head>
    <body>
        <div class="phone-frame">
            <div class="status-bar"><span>12:01</span><span>📶 🔋 100%</span></div>
            <div class="app-bar"><span>←</span><span>Paramètres</span></div>
            <div style="flex: 1;">
                <div class="category-title">Affichage</div>
                <div class="pref-item">
                    <div>
                        <div class="pref-title">Taille de police</div>
                        <div class="pref-summary">Moyenne (16 sp)</div>
                    </div>
                </div>
                <div class="pref-item">
                    <div>
                        <div class="pref-title">Couleur préférée (du texte)</div>
                        <div class="pref-summary">Bleu (#1976D2)</div>
                    </div>
                    <div style="width: 24px; height: 24px; border-radius: 12px; background: #1976D2;"></div>
                </div>

                <div class="category-title" style="margin-top: 10px;">Liste des contacts</div>
                <div class="pref-item">
                    <div>
                        <div class="pref-title">Activer l\'appel depuis la liste</div>
                        <div class="pref-summary">Bouton d\'appel sur chaque ligne</div>
                    </div>
                    <div class="switch-on"><div class="switch-thumb"></div></div>
                </div>
            </div>
            <div class="nav-bar"><div class="home-indicator"></div></div>
        </div>
    </body>
    </html>
    """

def get_v4_dialog_html():
    return f"""
    <!DOCTYPE html>
    <html>
    <head><meta charset="utf-8">
    <style>
        * {{ box-sizing: border-box; margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; }}
        body {{ background: transparent; display: flex; justify-content: center; align-items: center; padding: 20px; }}
        .phone-frame {{ width: 340px; height: 680px; background: rgba(0,0,0,0.6); border-radius: 36px; border: 9px solid #1E293B; box-shadow: 0 20px 40px rgba(0,0,0,0.25); display: flex; flex-direction: column; position: relative; overflow: hidden; justify-content: center; align-items: center; padding: 20px; }}
        .dialog-card {{ background: white; border-radius: 16px; padding: 22px; width: 100%; box-shadow: 0 10px 25px rgba(0,0,0,0.3); }}
        .dialog-title {{ font-size: 17px; font-weight: bold; color: #0F172A; margin-bottom: 10px; }}
        .dialog-msg {{ font-size: 13px; color: #475569; line-height: 1.5; margin-bottom: 20px; }}
        .dialog-actions {{ display: flex; justify-content: flex-end; gap: 12px; }}
        .btn-action {{ font-size: 14px; font-weight: bold; padding: 6px 12px; border-radius: 6px; cursor: pointer; }}
    </style>
    </head>
    <body>
        <div class="phone-frame">
            <div class="dialog-card">
                <div class="dialog-title">Confirmer la suppression</div>
                <div class="dialog-msg">Voulez-vous vraiment supprimer définitivement ce contact de la base SQLite ?</div>
                <div class="dialog-actions">
                    <span class="btn-action" style="color: #64748B;">ANNULER</span>
                    <span class="btn-action" style="color: #DC2626;">SUPPRIMER</span>
                </div>
            </div>
        </div>
    </body>
    </html>
    """

def run():
    with sync_playwright() as p:
        browser = p.chromium.launch()
        page = browser.new_page(viewport={"width": 420, "height": 760})

        # 1. First launch list (Default SQLite contacts)
        page.set_content(get_v4_list_html())
        p1 = os.path.join(SCREENSHOTS_DIR, "v4_01_liste_initiale_sqlite.png")
        page.locator(".phone-frame").screenshot(path=p1)
        print(f"Generated: {p1}")

        # 2. Settings screen
        page.set_content(get_v4_settings_html())
        p2 = os.path.join(SCREENSHOTS_DIR, "v4_02_ecran_parametres.png")
        page.locator(".phone-frame").screenshot(path=p2)
        print(f"Generated: {p2}")

        # 3. List with customized preferences: Blue color + Call button on each row!
        page.set_content(get_v4_list_html(font_size=16, font_color="#1976D2", show_call_buttons=True))
        p3 = os.path.join(SCREENSHOTS_DIR, "v4_03_preferences_appliquees.png")
        page.locator(".phone-frame").screenshot(path=p3)
        print(f"Generated: {p3}")

        # 4. Confirmation dialog for deletion
        page.set_content(get_v4_dialog_html())
        p4 = os.path.join(SCREENSHOTS_DIR, "v4_04_confirmation_suppression.png")
        page.locator(".phone-frame").screenshot(path=p4)
        print(f"Generated: {p4}")

        browser.close()

if __name__ == "__main__":
    run()
