import xml.etree.ElementTree as ET
import re

tree = ET.ElementTree()

out_tree = ET.Element('alchemy')
tree.parse('ingredients.xml')
games = list(tree.iter('game'))

for game in games:

    game_elem = ET.SubElement(out_tree, 'game')
    game_prefix = game.find('prefix').text
    ET.SubElement(game_elem, 'name').text = game.find('name').text
    ET.SubElement(game_elem, "prefix").text = game_prefix

    packages = list(game.iter('package'))

    game_effects = dict()

    for package in packages:
        package_elem = ET.SubElement(game_elem, 'package')
        ET.SubElement(package_elem, 'name').text = package.find('name').text

        ingredients = list(package.iter('ingredient'))
        for ingred in ingredients:
            ingred_elem = ET.SubElement(package_elem, 'ingredient')
            ingred_name = ingred.find('name').text
            ET.SubElement(ingred_elem, 'name').text = ingred_name
            image_name = game_prefix + '_' + re.sub(r"[^a-z]", '_', ingred_name.lower().replace('\'', ''))
            ET.SubElement(ingred_elem, 'image').text = image_name

            effectCodes = list(ingred.iter('name'))
            for name in effectCodes:
                effect_elem = ET.SubElement(ingred_elem, 'name')
                effect_elem.text = re.sub(r"[^a-z]", '_', name.text.lower())
                game_effects[effect_elem.text] = name.text

    game_effect_elem = ET.SubElement(game_elem, 'effectCodes')
    for effect_code, effect_name in game_effects.items():
        effect_elem = ET.SubElement(game_effect_elem, 'name')
        ET.SubElement(effect_elem, 'code').text = effect_code
        ET.SubElement(effect_elem, 'name').text = effect_name
        ET.SubElement(effect_elem, 'image').text = game_prefix + '_' + effect_code

with open('ingredients2.xml', 'wb') as output_file:
    output_file.write(ET.tostring(out_tree))
