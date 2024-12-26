# TODO список для проекта

## 1. Безопасность
- [ ] Разграничить неавторизованный доступ
- [ ] Регулярки против SQL инъекций

## 2. Функционал
- [x] Сделать проверку на дублирующиеся ссылки
  - Добавил SQL запрос в LinkRepository
  - Добавил проверку в LinkSelectorController (handleAddLinkSelectorForm)
- [x] Добавить возможность просмотра/изменения селекторов
  - Добавлен сонтроллер SelectorViewController
- [x] ~~Добавить возможность просмотра/изменения/удаления ссылок~~
  - лишнее
- [ ] Сделать обработку добавления изображения, если те отсутствуют на сайте
- [ ] Добавить логирование ошибок
- [x] Добавить возможность просмотра лога ошибок
- [x] Добавить страницу 404

## 3. Уведомления в ТГ
- [ ] Добавить возможность просмотра информации через ТГ
- [ ] Уведомления в ТГ по поводу изменения цены (возможно добавить какой-то порог, при котором они будут отправляться)
- [ ] Сделать возможность удаления отслеживания тура

## 4. Дизайн сайта
- [ ] Поработать с карточкой тура, вывод полной информации
- [ ] Добавить расположение тура
