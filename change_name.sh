grep -rEl --exclude=*/target/* --exclude=*/src-gen/* --exclude=*change_name.sh* --exclude=*_subtyping.ts* --exclude=*_runtime.str* "Entity" * | xargs sed -i "" "s/Entity/entityscala/g"
grep -rEl --exclude=*/target/* --exclude=*/src-gen/* --exclude=*change_name.sh* --exclude=*_subtyping.ts* --exclude=*_runtime.str* "Entity" * | xargs sed -i "" "s/Entity/entityscala/g"
find . -depth -name "*Entity*" -execdir sh -c 'mv {} $(echo {} | sed "s/Entity/entityscala/")' \;
find . -depth -name "*Entity*" -execdir sh -c 'mv {} $(echo {} | sed "s/Entity/entityscala/")' \;
