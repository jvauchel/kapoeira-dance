# at the beginning
docker compose build --no-cache
FRIES_IMPL=KO docker compose up -d

# run kapoeira on fries-factory
docker compose restart kapoeira-fries
# see file:///home/mrebiai/github.com/jvauchel/kapoeira-dance/target/reports/fries-factory/kapoeira-report.html
# or  file:///home/jvauchel/github.com/jvauchel/kapoeira-dance/target/reports/fries-factory/kapoeira-report.html

# restart fries-factory with implementation
docker compose stop fries-factory
FRIES_IMPL=OK docker compose up -d fries-factory

# run kapoeira on fries-factory
docker compose restart kapoeira-fries
# see file:///home/mrebiai/github.com/jvauchel/kapoeira-dance/target/reports/fries-factory/kapoeira-report.html
# or  file:///home/jvauchel/github.com/jvauchel/kapoeira-dance/target/reports/fries-factory/kapoeira-report.html

# run kapoeira on burger-factory
docker compose restart kapoeira-burger
# see file:///home/mrebiai/github.com/jvauchel/kapoeira-dance/target/reports/burger-factory/kapoeira-report.html
# or  file:///home/jvauchel/github.com/jvauchel/kapoeira-dance/target/reports/burger-factory/kapoeira-report.html

# run kapoeira on meal-factory
docker compose restart kapoeira-meal
# see file:///home/mrebiai/github.com/jvauchel/kapoeira-dance/target/reports/meal-factory/kapoeira-report.html
# or  file:///home/jvauchel/github.com/jvauchel/kapoeira-dance/target/reports/meal-factory/kapoeira-report.html

# run kapoeira on end-to-end
docker compose restart kapoeira-end-to-end
# see file:///home/mrebiai/github.com/jvauchel/kapoeira-dance/target/reports/end-to-end/kapoeira-report.html
# or  file:///home/jvauchel/github.com/jvauchel/kapoeira-dance/target/reports/end-to-end/kapoeira-report.html

# at the end
docker compose down