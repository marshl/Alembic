from bs4 import BeautifulSoup
import os
import requests
import sys
import re
from slugify import slugify
import xml.etree.cElementTree as ET
import xml.dom.minidom

config = {
    "morrowind": {
        "name": "Morrowind",
        "prefix": "mw",
        "levels": [
            "15-29 (1 effect)",
            "30-44 (2 effects)",
            "45-59 (3 effects)",
            "60-100 (all effects)",
        ],
        "packages": [
            {
                "name": "Morrowind",
                "code": "morrowind",
                "src": "https://en.uesp.net/wiki/Morrowind:Ingredients",
            },
            {
                "name": "Tribunal",
                "code": "tribunal",
                "src": "https://en.uesp.net/wiki/Tribunal:Ingredients",
            },
            {
                "name": "Bloodmoon",
                "code": "bloodmoon",
                "src": "https://en.uesp.net/wiki/Bloodmoon:Ingredients",
            },
        ],
    },
    "oblivion": {
        "name": "Oblivion",
        "prefix": "ob",
        "levels": [
            "1-24: Novice (1 effect)",
            "25-49: Apprentice (2 effects)",
            "50-74: Journeyman (3 effects)",
            "75-100: Expert/Master (all effects)",
        ],
        "packages": [
            {
                "name": "Oblivion",
                "code": "oblivion",
                "src": "https://en.uesp.net/wiki/Oblivion:Ingredients",
            },
            {
                "name": "Shivering Isles",
                "code": "shivering",
                "src": "https://en.uesp.net/wiki/Shivering:Ingredients",
            },
        ],
    },
    "skyrim": {
        "name": "Skyrim",
        "prefix": "sr",
        "packages": [
            {
                "name": "Skyrim",
                "code": "skyrim",
                "src": "https://en.uesp.net/wiki/Skyrim:Ingredients",
            }
        ],
    },
}


def get_package_file(package_code: str) -> str:
    return os.path.join("html", package_code + ".html")


def download_package_file(package_code: str, package_src: str):
    response = requests.get(package_src)
    with open(get_package_file(package_code), "w", encoding="utf8") as file:
        file.write(response.text)


def download_files():
    for game_key, game in config.items():
        for package in game["packages"]:
            download_package_file(package["code"], package["src"])


def parse_ingredient(first_row: BeautifulSoup, second_row: BeautifulSoup):
    ingredient = {}
    img_tag = first_row.select_one("img")
    ingredient["img_url"] = img_tag.attrs["src"]
    name_tag = first_row.select_one("a[title]")
    ingredient["description"] = first_row.select("td")[2].text
    ingredient["name"] = name_tag.text

    effects = second_row.select("td")[:4]
    other = second_row.select("td")[4:]
    value_tag, weight_tag = other[:2]
    ingredient["value"] = int(value_tag.text)
    ingredient["weight"] = float(weight_tag.text)
    ingredient["effects"] = []
    for effect_tag in effects:
        if not effect_tag.text:
            continue
        name = {}
        for link in effect_tag.select("a"):
            if link.select("img"):
                image_tag = link.select_one("img")
                if image_tag["alt"] in ["Magnitude", "Value"]:
                    continue
                name["img_url"] = link.select_one("img").attrs["src"]
            else:
                name["name"] = link.text

        if effect_tag.select_one('a[title="Value"]'):
            name["value_multiplier"] = float(effect_tag.select_one("b").text)
        if effect_tag.select_one('a[title="Magnitude"]'):
            name["magnitude_multiplier"] = float(effect_tag.select_one("b").text)

        name["key"] = slugify(name["name"], separator="_")
        ingredient["effects"].append(name)
    return ingredient


def parse_files():
    for game_key, game in config.items():
        for package in game["packages"]:
            with open(get_package_file(package["code"]), "r", encoding="utf8") as file:
                soup = BeautifulSoup(file, features="html.parser")
                table = soup.select("table.wikitable")[0]
                rows = table.select("tr")
                header = rows[0]
                rows = rows[1:]
                it = iter(rows)
                package["ingredients"] = list()
                for first_row in it:
                    second_row = next(it)
                    if first_row.text.strip() == "Special Ingredients":
                        break
                    ingredient = parse_ingredient(first_row, second_row)
                    package["ingredients"].append(ingredient)
                pass


def find_game_effects(game: dict):
    game["effects"] = {}
    for package in game["packages"]:
        for ingredient in package["ingredients"]:
            for name in ingredient["effects"]:
                if name["key"] in game["effects"]:
                    continue

                if "img_url" not in name:
                    img = game["prefix"] + "_" + "magic_hat"
                else:
                    if game["prefix"] == "ob":
                        matches = re.match(
                            r".+?icon-(?P<img>.+?)\.(png|jpg)", name["img_url"]
                        )
                    elif game["prefix"] == "sr":
                        matches = re.match(
                            r".+?icon-spell-(?P<img>.+?)\.(png|jpg)", name["img_url"]
                        )
                    else:
                        matches = re.match(
                            r".+?icon-name-(?P<img>.+?)\.(png|jpg)", name["img_url"]
                        )

                    img = game["prefix"] + "_" + matches["img"].lower()
                    img = img.replace("%27", "").replace("-", "_")
                game["effects"][name["key"]] = {"name": name["name"], "img": img}


def write_to_xml():

    root = ET.Element("alchemy")
    for game_key, game in config.items():
        game_element = ET.SubElement(root, "game")
        ET.SubElement(game_element, "name").text = game["name"]
        ET.SubElement(game_element, "prefix").text = game["prefix"]
        if "levels" in game:
            level_element = ET.SubElement(game_element, "levels")
            for level in game["levels"]:
                ET.SubElement(level_element, "name").text = level

        for package in game["packages"]:
            package_element = ET.SubElement(game_element, "package")
            ET.SubElement(package_element, "name").text = package["name"]
            for ingredient in package["ingredients"]:
                ingredient_element = ET.SubElement(package_element, "ingredient")
                ET.SubElement(ingredient_element, "name").text = ingredient["name"]
                ET.SubElement(ingredient_element, "description").text = ingredient[
                    "description"
                ]
                matches = re.match(
                    r".+?icon-ingredient-(?P<img>.+?)\.(png|jpg)", ingredient["img_url"]
                )
                img = game["prefix"] + "_" + matches["img"].lower()
                img = img.replace("%27", "").replace("-", "_")
                # img = game["prefix"] + "_" + slugify(ingredient["name"], separator="_")
                ET.SubElement(ingredient_element, "image").text = img

                ET.SubElement(ingredient_element, "value").text = str(
                    ingredient["value"]
                )
                ET.SubElement(ingredient_element, "weight").text = str(
                    ingredient["weight"]
                )

                for effect in ingredient["effects"]:
                    ET.SubElement(ingredient_element, "effect").text = effect["key"]
                    if "value_multiplier" in effect:
                        ET.SubElement(
                            ingredient_element, "value_multiplier"
                        ).text = str(effect["value_multiplier"])
                    if "magnitude_multiplier" in effect:
                        ET.SubElement(
                            ingredient_element, "magnitude_multiplier"
                        ).text = str(effect["magnitude_multiplier"])

        for effect_key, name in sorted(game["effects"].items(), key=lambda e: e[0]):
            effect_element = ET.SubElement(game_element, "effect")
            ET.SubElement(effect_element, "name").text = name["name"]
            ET.SubElement(effect_element, "code").text = effect_key
            ET.SubElement(effect_element, "image").text = name["img"]

    # doc = ET.SubElement(root, "doc")
    #
    # ET.SubElement(doc, "field1", name="blah").text = "some value1"
    # ET.SubElement(doc, "field2", name="asdfasd").text = "some vlaue2"

    tree = ET.ElementTree(root)

    tree.write("ingredients.xml")

    dom = xml.dom.minidom.parse("ingredients.xml")
    pretty_xml_as_string = dom.toprettyxml()
    with open("ingredients.xml", "w") as output_file:
        output_file.write(pretty_xml_as_string)


def main():
    if "--download" in sys.argv:
        download_files()

    parse_files()
    for game_key, game in config.items():
        find_game_effects(game)

    write_to_xml()


if __name__ == "__main__":
    main()
