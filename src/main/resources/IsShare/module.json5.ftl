{
  "module": {
    "name": "${module_name}",
    "type": "entry",
    "description": "$string:shared_desc",
    "mainElement": "${moduleUpName}Ability",
    "deviceTypes": [
      "phone",
      "tablet"
    ],
    "deliveryWithInstall": true,
    "installationFree": false,
    "pages": "$profile:main_pages",
    "abilities": [
      {
        "name": "${moduleUpName}Ability",
        "srcEntry": "./ets/${module_name}ability/${moduleUpName}Ability.ts",
        "description": "$string:shared_desc",
        "icon": "$media:icon",
        "label": "$string:shared_desc",
        "startWindowIcon": "$media:icon",
        "startWindowBackground": "$color:white",
        "exported": true,
        "skills": [
          {
            "entities": [
              "entity.system.home"
            ],
            "actions": [
              "action.system.home"
            ]
          }
        ]
      }
    ]
  }
}