#!bin/sh
for i in {0..9}
do
    echo "$i"*.ddem
    find . -name "$i*.ddem" | xargs -n 1 tail -n +2 >> a"$i"0.ddem
done
echo Finished