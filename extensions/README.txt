Web URL Convert rules: 

admin/UserAction/get.do

path[0] = admin
path[1] = UserAction
path[2] = get.do
path[last] = get.do
path[first] = admin
path[all] = admin/UserAction/get.do
path[withoutLast] = admin/UserAction/
path[lastNoSuffix] = get
->

path=${path[withoutLast]}
method=${path[lastNoSuffix]}